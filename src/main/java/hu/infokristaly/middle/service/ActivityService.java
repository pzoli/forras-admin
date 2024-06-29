package hu.infokristaly.middle.service;

import hu.infokristaly.back.domain.Alert;
import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.domain.Message;
import hu.infokristaly.back.domain.Subject;
import hu.exprog.beecomposit.back.model.SystemUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.SortOrder;

@Stateless
@Named
public class ActivityService implements Serializable {

    private static final long serialVersionUID = -2812358566616597537L;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private AlertService alertService;

    @Inject
    private ClientsService clientsService;

    @Inject
    private MessageService messageService;

    @SuppressWarnings("unchecked")
    public List<ScheduleEvent> loadEvents(Date start, Date end, TimeZone timeZone, Boolean resetAlert, Client client) {
        List<ScheduleEvent> eventList = null;
        if (client != null) {
            Calendar startDate = null;
            if (start != null) {
                startDate = new GregorianCalendar();
                startDate.setTime(start);
            }
            Calendar endDate = null;
            if (end != null) {
                endDate = new GregorianCalendar();
                endDate.setTime(end);
            }
            if ((timeZone != null) && (startDate != null) && (start != null) && timeZone.inDaylightTime(start)) {
                startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
                if (endDate != null) {
                    endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
                }
            }

            String dateFilter = null;
            if (startDate != null && endDate != null) {
                dateFilter = "e.startDate between :beginDate and :endDate";
            } else if (startDate != null) {
                dateFilter = "e.startDate >= :beginDate";
            } else if (endDate != null) {
                dateFilter = "e.startDate <= :endDate";
            }

            String select = "select e from EventHistory e join e.clients c " + (resetAlert != null ? "join e.subject s " : "") + "where c.id = :clientId " + (dateFilter != null ? "and " + dateFilter : "") + " and styleClass is null " + (resetAlert != null ? "and s.resetAlert=:resetAlert " : "") + "order by e.startDate desc";

            Query q = em.createQuery(select);

            if (startDate != null) {
                q.setParameter("beginDate", startDate.getTime());
            }

            if (endDate != null) {
                q.setParameter("endDate", endDate.getTime());
            }

            if (resetAlert != null) {
                q.setParameter("resetAlert", resetAlert);
            }
            q.setParameter("clientId", client.getId());
            eventList = (List<ScheduleEvent>) q.getResultList();
        } else {
            eventList = new ArrayList<ScheduleEvent>();
        }
        return eventList;
    }

    public String checkClients(SystemUser user, List<Client> clients, Date startDate) {
        int counter = 0;
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd. HH:mm");
        for (Client client : clients) {
            List<ScheduleEvent> events = loadEvents(startDate, null, null, true, client);
            Iterator<ScheduleEvent> iter = events.iterator();
            if (iter.hasNext()) {
                ScheduleEvent lastEvent = iter.next();
                Date lastEventDate = lastEvent.getStartDate();
                try {
                    ClientChanges nextVersion = clientsService.findClientNextChange(client, lastEventDate);
                    ClientChanges version = clientsService.getClientVersion(client, lastEventDate);
                    if ((nextVersion != null) && (version != null) && Boolean.TRUE.equals(version.getActive())) {
                        long nDay = ClientsService.getDiff(nextVersion.getPeriodStart(), lastEventDate);
                        if (nDay > AlertService.BACKALERT_DAY) {
                            Alert alert = new Alert();
                            alert.setTitle(dateFormatter.format(lastEventDate) + " - " + dateFormatter.format(nextVersion.getPeriodStart()));
                            alert.setClient(client);
                            alert.setClientType(version.getClientType());
                            alert.setActive(version.getActive());
                            alert.setLastVisit(lastEvent.getStartDate());
                            alert.setNDay(nDay);
                            List<Alert> sameAlerts = ((AlertService) alertService).findAlertWithTitle(alert);
                            if (sameAlerts.size() == 0) {
                                ((AlertService) alertService).persistAlert(alert);
                                counter++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                while (iter.hasNext()) {
                    try {
                        ScheduleEvent nextEvent = iter.next();

                        final Date nextEventDate = nextEvent.getStartDate();
                        long nDay = ClientsService.getDiff(lastEventDate, nextEventDate);
                        if (nDay > AlertService.BACKALERT_DAY) {
                            ClientChanges version;
                            try {
                                version = clientsService.getClientVersion(client, nextEventDate);
                            } catch (Exception e) {
                                version = null;
                            }
                            if ((version == null) || (version.getActive() == null) || Boolean.FALSE.equals(version.getActive())) {
                                continue;
                            }

                            Date lastDate = lastEvent.getStartDate();
                            List<ClientChanges> changes = clientsService.findClientAllChange(client, false);
                            changes = changes.stream().filter(c -> Boolean.FALSE.equals(c.getActive()) && (c.getPeriodStart() != null) && c.getPeriodStart().after(nextEventDate) && c.getPeriodStart().before(lastDate)).collect(Collectors.toList());
                            if (!changes.isEmpty()) {
                                lastEventDate = changes.get(changes.size() - 1).getPeriodStart();
                                nDay = ClientsService.getDiff(changes.get(0).getPeriodStart(), nextEventDate);
                                if (nDay <= AlertService.BACKALERT_DAY) {
                                    continue;
                                }
                            }
                            Alert alert = new Alert();
                            alert.setClientType(version.getClientType());
                            alert.setActive(version.getActive());
                            alert.setTitle((version != null ? version.getClientType().getTypename() + " : " : "") + dateFormatter.format(lastEventDate) + " - " + dateFormatter.format(nextEventDate));
                            alert.setClient(client);
                            alert.setLastVisit(lastEvent.getStartDate());
                            alert.setNDay(nDay);
                            List<Alert> sameAlerts = ((AlertService) alertService).findAlertWithTitle(alert);
                            if (sameAlerts.size() == 0) {
                                ((AlertService) alertService).persistAlert(alert);
                                counter++;
                            }
                        }
                        lastEvent = nextEvent;
                        lastEventDate = lastEvent.getStartDate();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "generált figyelmeztetések száma: " + String.valueOf(counter);
    }

    @SuppressWarnings("unchecked")
    public List<Client> visitorsList(Date start, Date end, TimeZone timeZone) {
        List<EventHistory> eventList = null;
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(start);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(end);
        if (timeZone.inDaylightTime(start)) {
            startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
            endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
        }

        String select = "select e.clients from EventHistory e where e.startDate between :beginDate and :endDate and styleClass is null";

        Query q = em.createQuery(select);

        q.setParameter("beginDate", startDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        eventList = (List<EventHistory>) q.getResultList();
        Iterator<EventHistory> iter = eventList.iterator();
        LinkedList<Client> result = new LinkedList<Client>();
        while (iter.hasNext()) {
            EventHistory item = iter.next();
            for (Client client : item.getClients()) {
                if (!result.contains(client)) {
                    result.add(client);
                }
            }
        }
        return result;
    }

    public String buildVisitorsQuery(Date start, Date end, Map<String, Object> filters) {
        String select = "select e.clients from EventHistory e fetch all properties where e.startDate between :beginDate and :endDate and styleClass is null";
        return select;
    }

    public int visitorCount(Date start, Date end, TimeZone timeZone, Map<String, Object> filters) {
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(start);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(end);
        if (timeZone.inDaylightTime(start)) {
            startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
            endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
        }

        String select = buildVisitorsQuery(start, end, filters);

        Query q = em.createQuery(select);
        q.setParameter("beginDate", startDate.getTime());
        q.setParameter("endDate", endDate.getTime());

        List<Client> content = q.getResultList();
        content = filterContent(content, filters);
        int result = content.size();
        return result;
    }

    private List<Client> filterContent(List<Client> content, Map<String, Object> filters) {
        if (filters != null && !filters.isEmpty()) {
            if (filters.containsKey("active")) {
                Boolean isActive = Boolean.valueOf((String) filters.get("active"));
                content = content.stream().filter(c -> (c.getActive() != null) && c.getActive() == isActive).collect(Collectors.toList());
            }
            if (filters.containsKey("clientType.typename")) {
                String typeName = (String) filters.get("clientType.typename");
                if ((typeName != null) && !(typeName.isEmpty())) {
                    content = content.stream().filter(c -> (c.getClientType() == null) || ((c.getClientType() != null) && c.getClientType().getTypename().startsWith(typeName))).collect(Collectors.toList());
                }
            }
            if (filters.containsKey("neve")) {
                String name = (String) filters.get("neve");
                if ((name != null) && !(name.isEmpty())) {
                    content = content.stream().filter(c -> (c.getNeve() == null) || ((c.getNeve() != null) && c.getNeve().startsWith(name))).collect(Collectors.toList());
                }
            }
            if (filters.containsKey("nyilvantartasiSzam")) {
                String nySzam = (String) filters.get("nyilvantartasiSzam");
                if ((nySzam != null) && !(nySzam.isEmpty())) {
                    content = content.stream().filter(c -> (c.getNyilvantartasiSzam() == null) || ((c.getNyilvantartasiSzam() != null) && c.getNyilvantartasiSzam().startsWith(nySzam))).collect(Collectors.toList());
                }
            }
        }
        return content;
    }

    public List<Client> loadVisitorList(Date start, Date end, TimeZone timeZone, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<Client> content = null;
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(start);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(end);
        if (timeZone.inDaylightTime(start)) {
            startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
            endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
        }

        String select = buildVisitorsQuery(start, end, filters);

        Query q = em.createQuery(select);
        q.setParameter("beginDate", startDate.getTime());
        q.setParameter("endDate", endDate.getTime());

        content = (List<Client>) q.getResultList();
        content = filterContent(content, filters);

        Comparator<Client> comparator;
        if (sortField != null) {
            if (sortField.equals("neve")) {
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    comparator = Comparator.comparing(Client::getNeve);
                } else {
                    comparator = Comparator.comparing(Client::getNeve).reversed();
                }
            } else {
                if (sortOrder.equals(SortOrder.ASCENDING)) {
                    comparator = Comparator.comparing(Client::getNyilvantartasiSzam);
                } else {
                    comparator = Comparator.comparing(Client::getNyilvantartasiSzam).reversed();
                }
            }
        } else {
            comparator = Comparator.comparing(Client::getNyilvantartasiSzam);
        }
        content = content.stream().sorted(comparator).skip(first).limit(pageSize).collect(Collectors.toList());

        return content;
    }

    @SuppressWarnings("unchecked")
    public List<Client> findAllClient(boolean alive, String active, Date endDate, String nySzam) {
        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("select c from Client c");
        String megszDatum;
        if (alive) {
            megszDatum = "c.megszDatum is null";
            if (endDate != null) {
                megszDatum = "((" + megszDatum + ") or c.megszDatum >= :endDate) and";
            } else {
                megszDatum = "((" + megszDatum + ") or c.megszDatum >= CURRENT_TIMESTAMP()) and";
            }
        } else {
            if (endDate != null) {
                megszDatum = "c.megszDatum < :endDate and";
            } else {
                megszDatum = "c.megszDatum < CURRENT_TIMESTAMP() and";
            }
        }

        queryBuff.append(" where " + megszDatum + " c.deletedDate is null ");
        if ((nySzam != null) && !nySzam.isEmpty()) {
            queryBuff.append(" and c.nyilvantartasiszam like ':nyszam'");
        }
        if ((active != null) && !active.isEmpty()) {
            queryBuff.append(" and c.active = " + active);
        }
        queryBuff.append(" order by neve asc");

        Query q = em.createQuery(queryBuff.toString());
        if (endDate != null) {
            q.setParameter("endDate", endDate);
        }
        if ((nySzam != null) && !nySzam.isEmpty()) {
            q.setParameter("nyszam", nySzam);
        }
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Client> findAllClient(Boolean active, boolean alive, String nySzam) {
        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("select c from Client c");
        String megszDatum;
        if (alive) {
            megszDatum = "c.megszDatum is null";
            megszDatum = "((" + megszDatum + ") or c.megszDatum >= CURRENT_TIMESTAMP()) and";
            queryBuff.append(" where " + megszDatum + " c.deletedDate is null ");
        }
        if (!(active == null)) {
            if (alive) {
                queryBuff.append(" and ");
            } else {
                queryBuff.append(" where ");
            }
            queryBuff.append(" c.active is " + active + " ");
        }
        if ((nySzam != null) && !nySzam.isEmpty()) {
            if (alive || (active != null)) {
                queryBuff.append(" and ");
            } else {
                queryBuff.append(" where ");
            }
            queryBuff.append(" c.nyilvantartasiSzam like :nyszam");
        }
        queryBuff.append(" order by neve asc");

        Query q = em.createQuery(queryBuff.toString());

        if ((nySzam != null) && !nySzam.isEmpty()) {
            q.setParameter("nyszam", nySzam);
        }
        return q.getResultList();
    }

    public String buildQuery(Date start, Date end, Boolean showActiveOnly) {
        StringBuffer select = new StringBuffer("select e from EventHistory e join e.clients c where ");
        if ((start != null) && (end != null)) {
            select.append("e.startDate between :beginDate and :endDate and");
        } else if (start != null) {
            select.append("e.startDate <= :beginDate and");
        }
        if (showActiveOnly != null) {
            select.append(" e.active is " + showActiveOnly + " and");
        }
        select.append(" c.id = :clientId and styleClass is null");
        return select.toString();
    }

    @SuppressWarnings("unchecked")
    public List<EventHistory> loadEvents(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<EventHistory> eventList = null;
        Client client = (Client) filters.get("client");
        Date start = (Date) filters.get("startDate");
        Date end = (Date) filters.get("endDate");
        Boolean showActiveOnly = (Boolean) filters.get("showActiveOnly");
        if (client != null) {
            String query = buildQuery(start, end, showActiveOnly) + " order by e.startDate desc";
            Query q = em.createQuery(query);

            if (start != null) {
                q.setParameter("beginDate", start);
            }
            if (end != null) {
                q.setParameter("endDate", end);
            }
            q.setParameter("clientId", client.getId());
            q.setFirstResult(first);
            q.setMaxResults(pageSize);
            eventList = (List<EventHistory>) q.getResultList();
        } else {
            eventList = new ArrayList<EventHistory>();
        }
        return eventList;
    }

    public int getCount(Map<String, Object> filters) {
        Client client = (Client) filters.get("client");
        Date start = (Date) filters.get("startDate");
        Date end = (Date) filters.get("endDate");
        int result = 0;
        if (client != null) {
            Boolean showActiveOnly = (Boolean) filters.get("showActiveOnly");
            String query = buildQuery(start, end, showActiveOnly);

            Query q = em.createQuery(query);

            if (start != null) {
                q.setParameter("beginDate", start);
            }
            if (end != null) {
                q.setParameter("endDate", end);
            }
            q.setParameter("clientId", client.getId());
            result = q.getResultList().size();
        }
        return result;
    }

}
