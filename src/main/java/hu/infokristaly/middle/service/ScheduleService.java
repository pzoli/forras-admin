package hu.infokristaly.middle.service;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.domain.EventTemplate;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.model.LogEntry;
import hu.infokristaly.back.model.LoggedFunction;
import hu.infokristaly.back.model.SystemUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.primefaces.component.schedule.Schedule;
import org.primefaces.model.ScheduleEvent;

@Stateless
public class ScheduleService implements Serializable {

    private static final long serialVersionUID = 2653279436367352527L;

    private static final long EVENT = 1L;

    @Inject
    private EntityManager em;

    @Inject
    private UserService userService;

    @Inject
    private ClientsService clientService;

    @Inject
    private LogService logService;

    @SuppressWarnings("unchecked")
    public List<ScheduleEvent> loadEvents(Date start, Date end, TimeZone timeZone, boolean isMeeting, SystemUser[] findUsers) {
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(start);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(end);
        if (timeZone.inDaylightTime(start)) {
            startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
            endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
        }

        String select = "from EventHistory e where e.startDate between :beginDate and :endDate and styleClass is " + (isMeeting ? "not null" : "null");

        if ((findUsers != null)) {
            if (findUsers.length > 0) {
                select += " and e.createdBy in (:createdBy) or e.eventId in (select e2.eventId from EventHistory e2 join e2.leaders l where styleClass is " + (isMeeting ? "not null" : "null") + " and e2.startDate between :beginDate and :endDate and l in (:users))";
            } else {
                select += " and e.createdBy is null";
            }
        }

        select += " order by e.startDate";

        Query q = em.createQuery(select);
        if (findUsers == null) {
            // request all event
        } else {
            List<SystemUser> uList = new ArrayList<SystemUser>();
            int nullIdx = ArrayUtils.indexOf(findUsers, null);
            if (nullIdx > -1) {
                findUsers = ArrayUtils.remove(findUsers, nullIdx);
                findUsers = ArrayUtils.add(findUsers, userService.getLoggedInSystemUser());
            }
            if (findUsers.length > 0) {
                uList = (List<SystemUser>) Arrays.asList(findUsers);
                q.setParameter("users", uList);
                q.setParameter("createdBy", uList);
            }
        }
        q.setParameter("beginDate", startDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        List<ScheduleEvent> eventList = (List<ScheduleEvent>) q.getResultList();
        return eventList;
    }

    public void persistEvent(EventHistory currentEvent) {
        List<Client> clients = currentEvent.getClients();
        List<Client> mergedClients = new ArrayList<Client>();
        for (Client item : clients) {
            mergedClients.add(em.merge(item));
        }
        List<SystemUser> users = currentEvent.getLeaders();
        List<SystemUser> mergedUsers = new ArrayList<SystemUser>();
        for (SystemUser item : users) {
            mergedUsers.add(em.merge(item));
        }
        currentEvent.setClients(mergedClients);
        currentEvent.setLeaders(mergedUsers);
        if (currentEvent.getEventId() == null) {
            em.persist(currentEvent);
        } else {
            em.merge(currentEvent);
        }
        logService.logUserActivity(currentEvent.getCreatedBy(), EVENT, LogService.CREATE, currentEvent);

    }

    public void persistEventFromEventTemplate(EventTemplate eventTemplate) {
        List<Client> clients = eventTemplate.getClients();
        clients = findAndUpdateClientList(clients);
        List<Client> refreshedClients = clients.stream().filter(Client::getActive).collect(Collectors.toList());

        List<SystemUser> leaders = eventTemplate.getLeaders();
        leaders = findAndUpdateUserList(leaders); 
        List<SystemUser> refreshedUsers =  leaders.stream().filter(SystemUser::isEnabled).collect(Collectors.toList());
        
        EventHistory newEvent = new EventHistory(eventTemplate, refreshedClients, refreshedUsers);
               
        em.persist(newEvent);
    }
    
    private List<Client> findAndUpdateClientList(List<Client> clients) {
        List<Client> refreshedClients = new LinkedList<Client>();
        if (clients != null) {
            for (Client client : clients) {
                Client refClient = em.find(Client.class, client.getId());
                if (refClient != null) {
                    refreshedClients.add(refClient);
                }
            }
        }
        return refreshedClients;
    }

    private List<SystemUser> findAndUpdateUserList(List<SystemUser> users) {
        List<SystemUser> refreshedUsers = new LinkedList<SystemUser>();
        for (SystemUser user : users) {
            SystemUser refUser = em.find(SystemUser.class, user.getUserid());
            if (refUser != null) {
                refreshedUsers.add(refUser);
            }
        }
        return refreshedUsers;
    }

    public void mergeEvent(EventHistory currentEvent) {
        em.merge(currentEvent);
        logService.logUserActivity(currentEvent.getCreatedBy(), EVENT, LogService.MODIFY, currentEvent);
    }

    /*
     * public Session getSession() { Session session = (Session)
     * em.getDelegate(); return session; }
     */

    public void delete(SystemUser user, EventHistory[] selectedEvents) {
        if (selectedEvents.length > 0) {
            for (EventHistory event : selectedEvents) {
                if ((event != null) && (event.getEventId() != null)) {
                    event = em.find(EventHistory.class, event.getEventId());
                    if (event != null) {
                        em.remove(event);
                        logService.logUserActivity(user, EVENT, LogService.REMOVE, event);
                    }
                }
            }
        }
    }

    public String addUserToEvent(Long eventId, String clientId, String pinCode) {
        String result = "OK";
        EventHistory eventHistory = new EventHistory();
        eventHistory.setEventId(eventId);
        eventHistory = findEvent(eventHistory);
        if (eventHistory != null) {
            SystemUser systemUser = new SystemUser();
            systemUser.setPinCode(pinCode);
            systemUser = userService.findByPIN(systemUser);
            if (systemUser != null) {
                Client client = new Client();
                client.setNyilvantartasiSzam(clientId);
                client = clientService.findByNySzam(client);
                if ((client != null)) {
                    List<Client> clients = eventHistory.getClients();
                    if (clients == null) {
                        eventHistory.setClients(new ArrayList<Client>());
                    } else if (!(eventHistory.getClients().contains(client))) {
                        eventHistory.getClients().add(client);
                        mergeEvent(eventHistory);
                    } else {
                        result = "DUPLICATED CLIENT ON EVENT";
                    }

                } else {
                    result = "CLIENT NOT FOUND";
                }
            } else {
                result = "USER NOT FOUND";
            }
        } else {
            result = "EVENT NOT FOUND";
        }
        return result;
    }

    public EventHistory findEvent(EventHistory eventHistory) {
        EventHistory result = em.find(EventHistory.class, eventHistory.getEventId());
        return result;
    }

    public List<EventHistory> loadEvents(Date start, Date end, String pinCode) {
        List<EventHistory> result = null;
        String select = "from EventHistory e" + " where e.startDate between :beginDate and :endDate and styleClass is null and e.createdBy = :createdBy" + " or e.eventId in " + "(select e2.eventId from EventHistory e2 join e2.leaders l " + " where styleClass is null and e2.startDate between :beginDate and :endDate and l.userid = :userid)" + " order by e.startDate desc";
        Query q = em.createQuery(select);
        SystemUser systemUser = new SystemUser();
        systemUser.setPinCode(pinCode);
        systemUser = userService.findByPIN(systemUser);
        if (systemUser != null) {
            q.setParameter("userid", systemUser.getUserid());
            q.setParameter("createdBy", systemUser);
            q.setParameter("beginDate", start);
            q.setParameter("endDate", end);
            result = (List<EventHistory>) q.getResultList();
        }
        return result;
    }

    public EventHistory find(Long eventId) {
        EventHistory result = em.find(EventHistory.class, eventId);
        return result;
    }

    public EventHistory find(EventHistory currentEvent) {
        EventHistory result = em.find(EventHistory.class, currentEvent.getEventId());
        return result;
    }

    public EventTemplate findTemplate(Long eventId) {
        EventTemplate result = em.find(EventTemplate.class, eventId);
        return result;
    }

}
