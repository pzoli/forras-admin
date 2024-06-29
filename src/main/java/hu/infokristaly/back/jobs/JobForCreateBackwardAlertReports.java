package hu.infokristaly.back.jobs;

import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.Message;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.ActivityService;
import hu.infokristaly.middle.service.AlertService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.LogService;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.UserService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.naming.InitialContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobForCreateBackwardAlertReports implements Job {

    private static final byte EVENT = 4;
    // private static long alertBorderTime = 1000L * 60 * 60 * 24 * ALERT_DAY;
    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");

    AlertService alertService;
    ActivityService activityService;
    ClientsService clientsService;
    UserService userService;
    LogService logService;
    MessageService messageService;

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
        Date startDate = (Date)context.getJobDetail().getJobDataMap().get("startdate");
        Client[] clientsArray = (Client[])context.getJobDetail().getJobDataMap().get("clients");

        Calendar executeCalendar = new GregorianCalendar();
        executeCalendar.setTime(executeTime);
        String msg = "ready";
        if (!isWeekend(executeCalendar) || instant) {
            SystemUser user = null;
            try {
                InitialContext ic = new InitialContext();
                clientsService = (ClientsService) ic.lookup("java:/global/forras-admin/ClientsService");
                userService = (UserService) ic.lookup("java:/global/forras-admin/UserService");
                alertService = (AlertService) ic.lookup("java:/global/forras-admin/AlertService");
                activityService = (ActivityService) ic.lookup("java:/global/forras-admin/ActivityService");
                logService = (LogService) ic.lookup("java:/global/forras-admin/LogService");
                messageService = (MessageService)ic.lookup("java:/global/forras-admin/MessageService");
                if ((activityService != null) && (activityService instanceof ActivityService)) {
                    if (userId != null) {
                        user = getUserById(userId);
                    }
                    List<Client> clients;
                    if (clientsArray != null) {
                        clients = Arrays.asList(clientsArray);
                    } else {
                        clients = clientsService.findAll(true, null, null, null);
                    }

                    msg = activityService.checkClients(user,clients,startDate);
                } else {
                    System.out.println("service is null");
                    msg = "Service not found";
                }
                sendMessage(user, "Figyelmeztetések frissítése", "Visszamenőleges figyelmeztetések generálása befejeződött ("+msg+")");
            } catch (Throwable t) {
                System.out.println("service error: " + t.getMessage());
                msg = "Service erro: "+t.getMessage();
            } finally {
                log(user, "Visszamenőleges figyelmeztetések generálása befejeződött ("+msg+")");
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

    private void sendMessage(SystemUser user,String title,String msg) {
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
