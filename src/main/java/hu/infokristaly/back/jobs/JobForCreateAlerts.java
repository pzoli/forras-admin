package hu.infokristaly.back.jobs;

import hu.infokristaly.back.domain.Alert;
import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.Message;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.ActivityService;
import hu.infokristaly.middle.service.AlertService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.LogService;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.UserService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.naming.InitialContext;

import org.primefaces.model.ScheduleEvent;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobForCreateAlerts implements Job {

    private static final byte EVENT = 4;
    // private static long alertBorderTime = 1000L * 60 * 60 * 24 * ALERT_DAY;
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");

    UserService userService;
    ClientsService clientsService;
    AlertService alertService;
    MessageService messageService;
    LogService logService;
    ActivityService activityService;

    private boolean isWeekend(Calendar executeCalendar) {
        boolean result = false;
        int weekDay = executeCalendar.get(Calendar.DAY_OF_WEEK);
        result = (weekDay == Calendar.SATURDAY) || (weekDay == Calendar.SUNDAY);
        return result;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        StringBuffer logBuffer = new StringBuffer();
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.applyPattern("yyyy-MM-dd. HH:mm");

        logBuffer.append(this.getClass().getSimpleName() + " quartz job started. (" + dateFormatter.format(new Date()) + ")");
        Date executeTime = context.getFireTime();
        Boolean instant = context.getJobDetail().getJobDataMap().getBoolean("instant");
        Long userId = context.getJobDetail().getJobDataMap().getLong("userid");
        Calendar executeCalendar = new GregorianCalendar();
        executeCalendar.setTime(executeTime);
        Calendar lastVisitTime = new GregorianCalendar();
        lastVisitTime.setTime(executeTime);
        lastVisitTime.add(Calendar.DATE, -1 * AlertService.ALERT_DAY);
        if (!isWeekend(executeCalendar) || instant) {
            try {
                InitialContext ic = new InitialContext();
                userService = (UserService) ic.lookup("java:/global/forras-admin/UserService");
                clientsService = (ClientsService) ic.lookup("java:/global/forras-admin/ClientsService");
                alertService = (AlertService) ic.lookup("java:/global/forras-admin/AlertService");
                messageService = (MessageService) ic.lookup("java:/global/forras-admin/MessageService");
                logService = (LogService) ic.lookup("java:/global/forras-admin/LogService");
                activityService = (ActivityService) ic.lookup("java:/global/forras-admin/ActivityService");
                if ((clientsService != null) && (clientsService instanceof ClientsService) && ((alertService != null) && (alertService instanceof AlertService)) && ((activityService != null) && (activityService instanceof ActivityService))) {
                    System.out.println("userService:" + clientsService);
                    System.out.println("alertService:" + alertService);
                    System.out.println("activityService:" + activityService);
                    List<Client> clients = ((ClientsService) clientsService).findAll(true, "true", executeTime, null);
                    Iterator<Client> iter = clients.iterator();
                    while (iter.hasNext()) {
                        Client item = iter.next();

                        List<Alert> alerts = ((AlertService) alertService).getSystemAlerts(item);
                        if (((alerts != null) && (alerts.size() > 0))) {
                            for (Alert alert : alerts) {
                                if (alert.getTitle() == null) {
                                    ((AlertService) alertService).remove(alert);
                                }
                            }
                        }

                        List<ScheduleEvent> events = ((ActivityService) activityService).loadEvents(lastVisitTime.getTime(), executeTime, timeZone, true, item);
                        if ((events == null) || ((events != null) && (events.size() == 0))) {
                            Alert alert = new Alert();
                            Timestamp lastVisit = ((ClientsService) clientsService).findLastVisit(item, true);
                            ClientChanges version = ((ClientsService) clientsService).findClientChange(item, lastVisit);
                            Date now = new Date();
                            long nDay = ClientsService.getDiff(now, lastVisit);

                            if (nDay >= AlertService.ALERT_DAY) {
                                alert.setTitle(null);
                                if (version != null) {
                                    alert.setClientType(version.getClientType());
                                    alert.setActive(version.getActive());
                                } else {
                                    alert.setClientType(item.getClientType());
                                    alert.setActive(item.getActive());
                                }
                                alert.setLastVisit(lastVisit);
                                alert.setNDay(nDay);
                                alert.setClient(item);
                                ((AlertService) alertService).persistAlert(alert);
                            }
                        }
                    }
                } else {
                    logBuffer.append("Service is not found.");
                }
            } catch (Throwable t) {
                logBuffer.append("Alert error: " + t.getMessage());

            } finally {
                SystemUser user = null;
                if (instant && (userId != null)) {
                    user = getUserById(userId);
                    sendMessage(user, "Figyelmeztetések frissítése", "Figyelmeztetések frissítése befejeződött.");
                }
                log(user, "Figyelmeztetések frissítése befejeződött. (" + logBuffer.toString() + ")");
            }
        }
        System.out.println("JobForCreateAlerts.class quartz job finished.");
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

    private void sendMessage(SystemUser user, String title, String msg) {
        Message message = new Message();
        Card card = new Card();
        card.setRecipientSystemUser(user);
        List<Card> cards = new LinkedList<Card>();
        cards.add(card);
        message.setCards(cards);
        message.setTitle(title);
        message.setSender(null);
        message.setSentDate(new Date());
        message.setMessage(msg);
        messageService.persist(message);
    }

}
