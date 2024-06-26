/**
 *
 * http://www.ibstaff.net/fmartinez/?p=57
 *
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.jobs.JobForCreateYearReport;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.UserService;
import hu.infokristaly.middle.service.YearReportService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

//import org.primefaces.context.RequestContext;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author pzoli
 * 
 */
@Named
@SessionScoped
public class YearReportManager implements Serializable {

    private static final long serialVersionUID = -5144647253213619374L;

    @Inject
    private YearReportService yearReportService;
    
    @Inject
    private UserService userService;
    
    private Integer reportStartDate;
    private Integer reportEndDate;

    private String[] selectedClientTypes = { "1" };
    private String visibleByActive = "true";

    @PostConstruct
    public void init() {
        reportEndDate = GregorianCalendar.getInstance().get(Calendar.YEAR);
        reportStartDate = reportEndDate - 1;
    }

    public void createReport() {
        if (reportStartDate == null) {
            FacesMessage message = new FacesMessage("Üres dátum!", "Kérem válassza ki a kimutatás dátumát!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        if (selectedClientTypes.length == 0) {
            FacesMessage message = new FacesMessage("Üres kliens típus!", "Kérem válassza ki a kimutatásban szereplő kliensek besorolását!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd. HH:mm:ss");

        FacesMessage message = new FacesMessage("Jelentés", "Jelentés gyártása elkezdődött (" + dateFormatter.format(new Date())+"). Értesítést kap, amint elkészült a jelentés.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        //RequestContext.getCurrentInstance().update("growl");
        
        boolean visibleAll = ((visibleByActive != null) && visibleByActive.isEmpty()) || visibleByActive == null;
        Boolean visibleByActiveBool = visibleAll ? null : "true".equalsIgnoreCase(visibleByActive);

        SystemUser user = userService.getLoggedInSystemUser();
        //yearReportService.createReport(user, reportStartDate, reportEndDate, visibleByActiveBool, selectedClientTypes);
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler.isStarted()) {
                String triggerId = "instant-system-trigger-";
                String jobId = "instant-system-job-";
                TriggerKey triggerKey = new TriggerKey(triggerId + "yearreport", "system");
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.add(Calendar.SECOND, 2);
                Trigger trigger = newTrigger().withIdentity(triggerKey).startAt(startCalendar.getTime()).build();
                JobDetail job = newJob(JobForCreateYearReport.class).withIdentity(jobId + "yearreport", "system").build();
                Long userId = userService.getLoggedInSystemUser().getId();
                job.getJobDataMap().put("instant",true);
                job.getJobDataMap().put("userid", userId);
                job.getJobDataMap().put("reportStartDate", reportStartDate);
                job.getJobDataMap().put("reportEndDate", reportEndDate);
                job.getJobDataMap().put("visibleByActiveBool", visibleByActiveBool);
                job.getJobDataMap().put("selectedClientTypes", selectedClientTypes);                
                scheduler.scheduleJob(job, trigger);
            }
        } catch (Exception ex) {

        }        

    }
    
    /**
     * @return the reportStartDate
     */
    public Integer getReportStartDate() {
        return reportStartDate;
    }

    /**
     * @param reportStartDate
     *            the reportStartDate to set
     */
    public void setReportStartDate(Integer reportStartDate) {
        this.reportStartDate = reportStartDate;
    }

    /**
     * @return the reportEndDate
     */
    public Integer getReportEndDate() {
        return reportEndDate;
    }

    /**
     * @param reportEndDate
     *            the reportEndDate to set
     */
    public void setReportEndDate(Integer reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    /**
     * @return the selectedClientTypes
     */
    public String[] getSelectedClientTypes() {
        return selectedClientTypes;
    }

    /**
     * @param selectedClientTypes
     *            the selectedClientTypes to set
     */
    public void setSelectedClientTypes(String[] selectedClientTypes) {
        this.selectedClientTypes = selectedClientTypes;
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

}
