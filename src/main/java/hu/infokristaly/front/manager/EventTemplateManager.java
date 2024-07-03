/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Zoltan Papp
 * 
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.EventTemplate;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.back.jobs.JobForCreateEventHistory;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.EventTemplateService;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.middle.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class EventTemplateManager implements Serializable {

    private static final long serialVersionUID = -3562322678573374746L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private EventTemplateService eventTemplateService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private ClientsService clientsService;

    @Inject
    private UserService userService;

    private Date nextStartDate = null;

    private EventTemplate currentEventTemplate;

    /** The lazy data model. */
    private LazyDataModel<EventTemplate> lazyDataModel;

    private Integer templateCount = null;

    private EventTemplate[] selectedTemplates = {};

    private boolean showNYSzam = false;

    private DualListModel<SystemUser> userModel;
    private DualListModel<Client> clientsModel;

    @PostConstruct
    private void init() {
        ArrayList<SystemUser> userList = new ArrayList<SystemUser>();
        ArrayList<Client> clientList = new ArrayList<Client>();
        clientsModel = new DualListModel<>(clientList, new ArrayList<Client>());
        userModel = new DualListModel<SystemUser>(userList, new ArrayList<SystemUser>());
    }

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<EventTemplate> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<EventTemplate>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyFileInfoDataModel] constructor finished.");
                }

                @Override
                public EventTemplate getRowData(String rowKey) {
                    Long primaryKey = Long.valueOf(rowKey);
                    return eventTemplateService.findTemplate(primaryKey);
                }

                @Override
                public Object getRowKey(EventTemplate eventTemplate) {
                    return eventTemplate.getId();
                }

                @Override
                public List<EventTemplate> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    templateCount = null;
                    this.setPageSize(pageSize);
                    this.actualfilters = filters;
                    List<EventTemplate> result = (List<EventTemplate>) eventTemplateService.findRange(first, pageSize, sortField, sortOrder, filters);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (templateCount == null) {
                        templateCount = eventTemplateService.count(actualfilters);
                    }
                    return templateCount;
                }

            };
        }
        return lazyDataModel;
    }

    public void createNewTemplate() {
        setCurrentEventTemplate(new EventTemplate());
        nextStartDate = null;
        ArrayList<SystemUser> userList = new ArrayList<SystemUser>();
        ArrayList<Client> clientList = new ArrayList<Client>();
        clientsModel = new DualListModel<>(clientList, new ArrayList<Client>());
        userModel = new DualListModel<SystemUser>(userList, new ArrayList<SystemUser>());        
        setLeaders();
        currentEventTemplate.setCreatedBy(userService.getLoggedInSystemUser());
        currentEventTemplate.setLastEmailAddress(userService.getLoggedInSystemUser().getEmailAddress());
        currentEventTemplate.setCreatedDate(new Date());
        currentEventTemplate.setEnabled(false);
    }

    public void onEdit(RowEditEvent event) {
        EventTemplate eventTemplate = (EventTemplate) event.getObject();

        FacesMessage msg = new FacesMessage("Sablon adatai átszerkesztve", eventTemplate.getTitle());
        EventTemplate lastEventTemplate = null;
        if (eventTemplate.getId() != null) {
            lastEventTemplate = eventTemplateService.findTemplate(eventTemplate.getId());
        }
        eventTemplateService.persistTemplate(eventTemplate);

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = eventTemplate.getLastEmailAddress() + "-trigger-" + eventTemplate.getId();
                String jobId = eventTemplate.getLastEmailAddress() + "-job-" + eventTemplate.getId();
                if (eventTemplate.getEnabled()) {

                    TriggerKey triggerKey = new TriggerKey(triggerId, "users");
                    Trigger trigger = null;
                    int period = eventTemplate.getPeriod() * 24;

                    if ((trigger = scheduler.getTrigger(triggerKey)) != null) {
                        if (((lastEventTemplate != null) && (lastEventTemplate.getPeriodStartDate() != null) && (lastEventTemplate.getPeriodStartDate().compareTo(eventTemplate.getPeriodStartDate()) != 0)) || (!lastEventTemplate.getPeriod().equals(eventTemplate.getPeriod()))) {
                            Trigger newtrigger = newTrigger().withIdentity(triggerKey).startAt(eventTemplate.getPeriodStartDate()).withSchedule(simpleSchedule().withIntervalInHours(period).repeatForever()).build();
                            scheduler.rescheduleJob(triggerKey, newtrigger);
                        } else {
                            scheduler.resumeTrigger(trigger.getKey());
                        }
                    } else {
                        EventTemplate eventHistory = scheduleService.findTemplate(eventTemplate.getId());
                        if (eventHistory == null) {
                            msg = new FacesMessage("A sablonhoz tartozó eventHistory nem található!", eventTemplate.getTitle());
                        } else {
                            JobDetail job = newJob(JobForCreateEventHistory.class).withIdentity(jobId, "users").build();
                            JobDataMap dataMap = job.getJobDataMap();

                            dataMap.put("template", eventTemplate);

                            Date startAt = getTriggerStartTime(eventTemplate);
                            trigger = newTrigger().withIdentity(triggerKey).startAt(startAt).withSchedule(simpleSchedule().withIntervalInHours(period).repeatForever()).build();
                            scheduler.scheduleJob(job, trigger);
                        }
                    }
                } else {
                    TriggerKey triggerKey = new TriggerKey(triggerId, "users");
                    scheduler.pauseTrigger(triggerKey);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private Date getTriggerStartTime(EventTemplate eventTemplate) {
        Date startAt = eventTemplate.getPeriodStartDate();
        Calendar periodStartTime = Calendar.getInstance();
        if (startAt != null) {
            periodStartTime.setTime(startAt);
        } else {
            periodStartTime.setTime(new Date());
        }
        return periodStartTime.getTime();
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Sablon módosítása visszavonva", ((EventTemplate) event.getObject()).getCommentOnEvent());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void persistCurrent() {
        eventTemplateService.persistTemplate(currentEventTemplate);
        createNewTemplate();
    }

    public void persistItem(EventTemplate item) {
        eventTemplateService.persistTemplate(item);
        createNewTemplate();
    }

    public EventTemplate[] getSelectedTemplates() {
        return selectedTemplates;
    }

    /**
     * @return the showNYSzam
     */
    public boolean isShowNYSzam() {
        return showNYSzam;
    }

    /**
     * @param showNYSzam the showNYSzam to set
     */
    public void setShowNYSzam(boolean showNYSzam) {
        this.showNYSzam = showNYSzam;
    }

    public void setSelectedTemplates(EventTemplate[] selectedTemplates) {
        this.selectedTemplates = selectedTemplates;
    }

    public void deleteTemplates() {
        Scheduler scheduler;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            for (EventTemplate item : selectedTemplates) {
                String fixPartJob = item.getLastEmailAddress() + "-job-";
                String jobId = fixPartJob + item.getId();
                eventTemplateService.deleteTemplate(item);
                scheduler.deleteJob(new JobKey(jobId, "users"));
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the currentEventHistory
     */
    public EventTemplate getCurrentEventTemplate() {
        return currentEventTemplate;
    }

    /**
     * @param currentEventHistory
     *            the currentEventHistory to set
     */
    public void setCurrentEventTemplate(EventTemplate currentEventTemplate) {
        this.currentEventTemplate = currentEventTemplate;
    }

    /**
     * @return the nextStartDate
     */
    public Date getNextStartDate() {
        return nextStartDate;
    }

    /**
     * @param nextStartDate
     *            the nextStartDate to set
     */
    public void setNextStartDate(Date nextStartDate) {
        this.nextStartDate = nextStartDate;
    }

    /**
     * @return the userModel
     */
    public DualListModel<SystemUser> getUserModel() {
        return userModel;
    }

    /**
     * @param userModel the userModel to set
     */
    public void setUserModel(DualListModel<SystemUser> userModel) {
        this.userModel = userModel;
    }

    /**
     * @return the clientsModel
     */
    public DualListModel<Client> getClientsModel() {
        return clientsModel;
    }

    /**
     * @param clientsModel the clientsModel to set
     */
    public void setClientsModel(DualListModel<Client> clientsModel) {
        this.clientsModel = clientsModel;
    }

    public void viewCurrentItem(EventTemplate template) {
        currentEventTemplate = scheduleService.findTemplate(template.getId());
        setClients();
        setLeaders();
        String jobId = template.getLastEmailAddress() + "-job-" + template.getId();
        nextStartDate = null;
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail detail = scheduler.getJobDetail(new JobKey(jobId, "users"));
            if (detail != null) {
                JobDataMap map = detail.getJobDataMap();
                String triggerId = template.getLastEmailAddress() + "-trigger-" + template.getId();
                Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerId,"users"));
                if (trigger != null) {
                    nextStartDate = trigger.getNextFireTime();
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    
    public void setClients() {
        if (currentEventTemplate != null) {
            List<Client> clientsList = new ArrayList<Client>();
            if (currentEventTemplate.getGroupForClients() != null) {
                if (currentEventTemplate.getGroupForClients().getId() == -1) {
                    clientsList = clientsService.findAll(true, null, currentEventTemplate.getStartDate(), null); //nameLike elvetve 2016.02.23.                    
                } else {
                    clientsList = currentEventTemplate.getGroupForClients().getClients();
                }
            }
            
            if ((currentEventTemplate.getClients() == null) || (currentEventTemplate.getClients().size() == 0)) {
                currentEventTemplate.setClients(new ArrayList<Client>());
            } else {
                for (Client client : currentEventTemplate.getClients()) {
                    if (clientsList.contains(client)) {
                        clientsList.remove(client);
                    }
                }
                Collections.sort(currentEventTemplate.getClients());
            }
            if (currentEventTemplate.getClients() != null) {
                setClientsModel(new DualListModel<Client>(clientsList, currentEventTemplate.getClients()));
            } else {
                setClientsModel(new DualListModel<Client>(clientsList, new ArrayList<Client>()));
            }
        } else {
            setClientsModel(new DualListModel<Client>(new ArrayList<Client>(), new ArrayList<Client>()));
        }
    }

    private void setLeaders() {
        try {
            ArrayList<SystemUser> userList = new ArrayList<SystemUser>(userService.findAll(true));
            if ((currentEventTemplate.getLeaders() == null) || (currentEventTemplate.getLeaders().size() == 0)) {
                SystemUser user = userService.getLoggedInSystemUser();
                currentEventTemplate.setLeaders(new ArrayList<SystemUser>());
                currentEventTemplate.getLeaders().add(user);
                if (userList.contains(user)) {
                    userList.remove(user);
                }
            } else {
                for (SystemUser user : currentEventTemplate.getLeaders()) {
                    if (userList.contains(user)) {
                        userList.remove(user);
                    }
                }
            }
            setUserModel(new DualListModel<SystemUser>(userList, currentEventTemplate.getLeaders()));
        } catch (Exception ex) {
            log.severe(ex.getLocalizedMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void onTransferClient(TransferEvent event) {
        List<Client> clients = (List<Client>) event.getItems();
        Iterator<Client> iter = clients.iterator();
        if (event.isAdd()) {
            while (iter.hasNext()) {
                Client client = iter.next();
                currentEventTemplate.getClients().add(client);
            }
        } else {
            while (iter.hasNext()) {
                Client client = iter.next();
                currentEventTemplate.getClients().remove(client);
            }
        }
        Collections.sort(clients);
    }

    @SuppressWarnings("unchecked")
    public void onTransferLeader(TransferEvent event) {
        List<SystemUser> leaders = (List<SystemUser>) event.getItems();
        Iterator<SystemUser> iter = leaders.iterator();
        if (event.isAdd()) {
            while (iter.hasNext()) {
                SystemUser user = iter.next();
                currentEventTemplate.getLeaders().add(user);
            }
        } else {
            while (iter.hasNext()) {
                SystemUser user = iter.next();
                currentEventTemplate.getLeaders().remove(user);
            }
        }
    }

    public void onGroupChangeListener(ValueChangeEvent event) {        
        currentEventTemplate.setGroupForClients((GroupForClients) event.getNewValue());
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        UIComponent pickList = root.findComponent("timerForm:eventTabs:pickList");
        if ((pickList != null) && (pickList instanceof DataTable)) {
            ((DataTable) pickList).setFirst(0);
        }
        setClients();
    }

    public void onSubjectChangeListener(AjaxBehaviorEvent event) {
        if (currentEventTemplate.getSubject() != null) {
            currentEventTemplate.setTitle(currentEventTemplate.getSubject().getTitle());
        }
    }

}
