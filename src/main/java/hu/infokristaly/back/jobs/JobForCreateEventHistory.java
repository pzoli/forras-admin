package hu.infokristaly.back.jobs;

import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.EventTemplate;
import hu.infokristaly.back.domain.Message;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.LogService;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.middle.service.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.InitialContext;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobForCreateEventHistory implements Job {

    private static final byte EVENT = 4;

    ScheduleService service;
    UserService userService;
    MessageService messageService;
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
        Calendar executeCalendar = new GregorianCalendar();
        executeCalendar.setTime(executeTime);
        JobDataMap dataMap = context.getMergedJobDataMap();
        Object templateObj = dataMap.get("template");
        EventTemplate template = (EventTemplate) templateObj;
        if (!isWeekend(executeCalendar) || template.getEnabledOnWeekend()) {
            Long eventId = template.getId();
            System.out.println(new Date() + ", TRIGGER: " + context.getTrigger().getKey() + " with template: " + template);
            try {
                InitialContext ic = new InitialContext();
                service = (ScheduleService)ic.lookup("java:/global/forras-admin/ScheduleService");
                if ((service != null) && (service instanceof ScheduleService)) {
                    System.out.println("service:" + service);
                    EventTemplate eventTemplate = ((ScheduleService) service).findTemplate(eventId);
                    if (eventTemplate != null) {
                        updateEventDates(eventTemplate, context.getFireTime());
                        ((ScheduleService) service).persistEventFromEventTemplate(eventTemplate);
                    } else {
                        System.out.println("Event is null");
                    }
                } else {
                    System.out.println("service is null");
                }
            } catch (Throwable t) {
                System.out.println("service lookup failed");
            }
        }
        System.out.println(this.getClass().getSimpleName() + " quartz job finished.");
    }

    private void updateEventDates(EventTemplate eventTemplate, Date fired) {
        Date startDate = eventTemplate.getStartDate();
        Calendar startDateCalendar = new GregorianCalendar();
        startDateCalendar.setTime(startDate);
        int startHour = startDateCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = startDateCalendar.get(Calendar.MINUTE);

        startDateCalendar.setTime(fired);
        startDateCalendar.set(Calendar.HOUR_OF_DAY, startHour);
        startDateCalendar.set(Calendar.MINUTE, startMinute);
        startDateCalendar.set(Calendar.SECOND, 0);
        startDateCalendar.set(Calendar.MILLISECOND, 0);
        eventTemplate.setStartDate(startDateCalendar.getTime());

        Date endDate = eventTemplate.getEndDate();
        Calendar endDateCalendar = new GregorianCalendar();
        endDateCalendar.setTime(endDate);
        int endHour = endDateCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = endDateCalendar.get(Calendar.MINUTE);

        endDateCalendar.setTime(fired);
        endDateCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        endDateCalendar.set(Calendar.MINUTE, endMinute);
        endDateCalendar.set(Calendar.SECOND, 0);
        endDateCalendar.set(Calendar.MILLISECOND, 0);

        eventTemplate.setEndDate(endDateCalendar.getTime());
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
