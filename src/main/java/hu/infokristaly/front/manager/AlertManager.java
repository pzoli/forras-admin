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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import hu.infokristaly.back.domain.Alert;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.jobs.JobForCreateAlerts;
import hu.infokristaly.back.jobs.JobForCreateBackwardAlertReports;
import hu.infokristaly.back.model.AppProperties;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.ActivityService;
import hu.infokristaly.middle.service.AlertService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class AlertManager implements Serializable {

    private static final long serialVersionUID = -7653931056704192413L;

    private static final int PERIOD_IN_HOURS = 24;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private AlertService alertService;

    @Inject
    private ActivityService activityService;

    @Inject
    private ClientsService clientService;

    @Inject
    private UserService userService;

    @Inject
    private AppProperties appProperties;

    private Integer count = null;

    private Alert newAlert;

    private Client alertedClient;

    private String alertedClientId;

    private Alert[] selectedAlerts = {};

    private String[] visibleClientTypes = { "1" };

    private String visibleByActive = "true";

    /** The lazy data model. */
    private LazyDataModel<Alert> lazyDataModel;

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<Alert> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<Alert>() {

                private static final long serialVersionUID = -7060145572783088084L;
                private Map<String, Object> actualfilters;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyAlertDataModel] constructor finished.");
                }

                @Override
                public Alert getRowData(String rowKey) {
                    Alert alert = new Alert();
                    alert.setId(Integer.valueOf(rowKey));
                    return alertService.find(alert);
                }

                @Override
                public Object getRowKey(Alert alert) {
                    return alert.getId();
                }

                @Override
                public List<Alert> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    this.setPageSize(pageSize);
                    filters.put("version.type", visibleClientTypes);
                    filters.put("version.active", visibleByActive);
                    this.actualfilters = filters;
                    count = null;
                    if (sortField == null) {
                        sortField = "nyilvantartasiSzam";
                    }
                    List<Alert> result = (List<Alert>) alertService.findRange(first, pageSize, sortField, sortOrder, filters);
                    log.log(Level.INFO, "[LazyAlertDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = alertService.count(actualfilters);
                    }
                    return count;
                }

            };
        }
        return lazyDataModel;
    }

    @PostConstruct
    public void init() {

    }

    /**
     * @return the alertedClient
     */
    public Client getAlertedClient() {
        return alertedClient;
    }

    /**
     * @param alertedClient
     *            the alertedClient to set
     */
    public void setAlertedClient(Long id) {
        Client aClient = new Client();
        aClient.setId(id);
        aClient = clientService.find(aClient);
        this.alertedClient = aClient;
    }

    public void createNewAlert() {
        setNewAlert(new Alert());
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Figyelmeztetés adatai átszerkesztve", ((Alert) event.getObject()).getTitle());
        alertService.persistAlert((Alert) event.getObject());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Figyelmeztetés módosítása visszavonva", ((Alert) event.getObject()).getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void save(Alert alert) {
        alertedClient = new Client();
        alertedClient.setId(Long.valueOf(alertedClientId));
        alertedClient = clientService.find(alertedClient);
        alert.setClient(alertedClient);
        alert.setCreatedBy(userService.getLoggedInSystemUser());
        alertService.persistAlert(alert);
    }

    public Alert getNewAlert() {
        return newAlert;
    }

    public void setNewAlert(Alert newAlert) {
        this.newAlert = newAlert;
    }

    public void persistCurrent() {
        save(newAlert);
    }

    public Alert[] getSelectedAlerts() {
        return selectedAlerts;
    }

    public void setSelectedAlerts(Alert[] selectedAlerts) {
        this.selectedAlerts = selectedAlerts;
    }

    public void deleteSelected() {
        List<Client> clients = new LinkedList<Client>();
        for (Alert alert : selectedAlerts) {
            if (!clients.contains(alert.getClient())) {
                clients.add(alert.getClient());
            }
        }
        alertService.delete(selectedAlerts);
        activityService.checkClients(null, clients, appProperties.getDefaultAlertManagerStartDate());

    }

    public boolean isScheduled() {
        boolean result = false;
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "system-trigger-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "alerts", "system");
                Trigger trigger = scheduler.getTrigger(triggerKey);
                result = trigger.mayFireAgain();
            }
        } catch (Exception e) {

        }
        return result;
    }

    public void startSystemJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "system-trigger-";
                String jobId = "system-job-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "alerts", "system");
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.set(Calendar.HOUR_OF_DAY, appProperties.getSystemTimerStartHour());
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);
                startCalendar.set(Calendar.MILLISECOND, 0);
                Trigger trigger = newTrigger().withIdentity(triggerKey).startAt(startCalendar.getTime()).withSchedule(simpleSchedule().withIntervalInHours(PERIOD_IN_HOURS).repeatForever()).build();
                JobDetail job = newJob(JobForCreateAlerts.class).withIdentity(jobId + "alerts", "system").build();
                Long userId = userService.getLoggedInSystemUser().getId();
                job.getJobDataMap().put("instant", false);
                job.getJobDataMap().put("userid", userId);
                scheduler.scheduleJob(job, trigger);
            }
        } catch (Exception ex) {
        	System.err.print(ex);
        }
    }

    public void stopSystemJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String jobId = "system-job-";
                scheduler.deleteJob(new JobKey(jobId + "alerts", "system"));
            }
        } catch (Exception ex) {

        }
    }

    public void pauseSystemJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "system-trigger-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "alerts", "system");
                scheduler.pauseTrigger(triggerKey);
            }
        } catch (Exception ex) {

        }
    }

    public void resumeSystemJobs() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "system-trigger-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "alerts", "system");
                scheduler.resumeTrigger(triggerKey);
            }
        } catch (Exception ex) {

        }
    }

    public void startAlertJob() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "instant-system-trigger-";
                String jobId = "instant-system-job-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "alerts", "system");
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.add(Calendar.SECOND, 10);
                Trigger trigger = newTrigger().withIdentity(triggerKey).startAt(startCalendar.getTime()).build();
                JobDetail job = newJob(JobForCreateAlerts.class).withIdentity(jobId + "alerts", "system").build();
                Long userId = userService.getLoggedInSystemUser().getId();
                job.getJobDataMap().put("instant", true);
                job.getJobDataMap().put("userid", userId);
                scheduler.scheduleJob(job, trigger);
                FacesMessage msg = new FacesMessage("Figyelmeztetés ütemezve 10 másodpercen belül");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception ex) {
            log.warning(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void startBackwardAlertsJob() {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "instant-system-trigger-";
                String jobId = "instant-system-job-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "backwardalerts", "system");
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.add(Calendar.SECOND, 10);
                Trigger trigger = newTrigger().withIdentity(triggerKey).startAt(startCalendar.getTime()).build();
                JobDetail job = newJob(JobForCreateBackwardAlertReports.class).withIdentity(jobId + "backalerts", "system").build();
                Long userId = userService.getLoggedInSystemUser().getId();
                job.getJobDataMap().put("instant", true);
                job.getJobDataMap().put("startdate", appProperties.getDefaultAlertManagerStartDate());
                job.getJobDataMap().put("userid", userId);
                scheduler.scheduleJob(job, trigger);
                FacesMessage msg = new FacesMessage("Visszamenőleges figyelmeztetések ütemezve 10 másodpercen belül");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception ex) {

        }
    }

    /**
     * @return the visibleClientTypes
     */
    public String[] getVisibleClientTypes() {
        return visibleClientTypes;
    }

    /**
     * @param visibleClientTypes
     *            the visibleClientTypes to set
     */
    public void setVisibleClientTypes(String[] visibleClientTypes) {
        this.visibleClientTypes = visibleClientTypes;
    }

    /**
     * @return the visibleByActive
     */
    public String getVisibleByActive() {
        return visibleByActive;
    }

    /**
     * @param visibleByActive
     *            the visibleByActive to set
     */
    public void setVisibleByActive(String visibleByActive) {
        this.visibleByActive = visibleByActive;
    }

    /**
     * @return the alertedClientId
     */
    public String getAlertedClientId() {
        return alertedClientId;
    }

    /**
     * @param alertedClientId
     *            the alertedClientId to set
     */
    public void setAlertedClientId(String alertedClientId) {
        this.alertedClientId = alertedClientId;
    }

    public void deleteAllAlerts() {
        alertService.deleteAllAlerts();
        FacesMessage msg = new FacesMessage("A figyelmeztetéseket töröltük.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
