package hu.infokristaly.back.jobs;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.DailyReportService;
import hu.infokristaly.middle.service.LogService;
import hu.infokristaly.middle.service.UserService;
import hu.infokristaly.middle.service.YearReportService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.naming.InitialContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobForCreateYearReport implements Job {

    private static final int ALERT_DAY = 25;
    private static final byte EVENT = 4;
    // private static long alertBorderTime = 1000L * 60 * 60 * 24 * ALERT_DAY;
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");

    YearReportService yearReportService;
    UserService userService;
    LogService logService;

    private boolean isWeekend(Calendar executeCalendar) {
        boolean result = false;
        int weekDay = executeCalendar.get(Calendar.DAY_OF_WEEK);
        result = (weekDay == Calendar.SATURDAY) || (weekDay == Calendar.SUNDAY);
        return result;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(this.getClass().getSimpleName() + " quartz job started.");
        Date executeTime = context.getFireTime();
        Boolean instant = context.getJobDetail().getJobDataMap().getBoolean("instant");
        Long userId = context.getJobDetail().getJobDataMap().getLong("userid");
        Integer reportStartDate = (Integer) context.getJobDetail().getJobDataMap().get("reportStartDate");
        Integer reportEndDate = (Integer) context.getJobDetail().getJobDataMap().get("reportEndDate");
        Boolean visibleByActiveBool = (Boolean) context.getJobDetail().getJobDataMap().get("visibleByActiveBool");
        String[] selectedClientTypes = (String[]) context.getJobDetail().getJobDataMap().get("selectedClientTypes");

        Calendar executeCalendar = new GregorianCalendar();
        executeCalendar.setTime(executeTime);
        Calendar lastVisitTime = new GregorianCalendar();
        lastVisitTime.setTime(executeTime);
        lastVisitTime.add(Calendar.DATE, -1 * ALERT_DAY);
        if (!isWeekend(executeCalendar) || instant) {
            try {
                InitialContext ic = new InitialContext();
                userService = (UserService) ic.lookup("java:/global/forras-admin/UserService");
                yearReportService = (YearReportService) ic.lookup("java:/global/forras-admin/YearReportService");
                logService = (LogService) ic.lookup("java:/global/forras-admin/LogService");
                SystemUser user = null;
                if (userId != null) {
                    user = new SystemUser();
                    user.setId(userId);
                    user = userService.find(user);
                }                
                if ((yearReportService != null) && (yearReportService instanceof YearReportService)) {
                    yearReportService.createReport(user, reportStartDate, reportEndDate, visibleByActiveBool, selectedClientTypes);
                } else {
                    System.out.println("service is null");
                }
                log(user, "Éves jelentés gyártása befejeződött");
            } catch (Throwable t) {
                System.out.println("service error: " + t.getMessage());
            }
        }
        System.out.println(this.getClass().getSimpleName() + " quartz job finished.");
    }

    private SystemUser getUserById(Long userId) {
        SystemUser user = new SystemUser();
        user.setId(userId);
        user = userService.find(user);
        return user;
    }

    private void log(SystemUser user, String msg) {
        logService.logUserActivity(user, EVENT, LogService.EXECUTE, this.getClass().getSimpleName() + ":" + msg);
    }

}
