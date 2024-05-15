package hu.infokristaly.rs.webservices;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.middle.service.ScheduleService;

@Path("rest/getTodayEventsForUser")
public class GetUserEvents {

    @GET()
    @Produces("text/plain; charset=UTF-8")
    public String getTodayEvents(@QueryParam("pin") String pinCode) {
        InitialContext ic;
        List<EventHistory> events = null;
        StringBuffer resultBuff = new StringBuffer();
        try {
            ic = new InitialContext();
            // java:global/forras-admin/ScheduleSercive!hu.infokristaly.middle.service.ScheduleService
            Object scheduleService = ic.lookup("java:/global/forras-admin/ScheduleService");
            if ((scheduleService != null) && (scheduleService instanceof ScheduleService)) {
                System.out.println("service:" + scheduleService);
                Calendar start = GregorianCalendar.getInstance();
                start.set(Calendar.HOUR_OF_DAY, 0);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.MILLISECOND, 0);
                Calendar end = GregorianCalendar.getInstance();
                end.set(Calendar.HOUR_OF_DAY, 24);
                end.set(Calendar.MINUTE, 0);
                end.set(Calendar.SECOND, 0);
                end.set(Calendar.MILLISECOND, 0);
                Date startDate = start.getTime();
                Date endDate = end.getTime();
                events = ((ScheduleService) scheduleService).loadEvents(startDate, endDate, pinCode);
                if (events != null) {
                    for (EventHistory event : events) {
                        String title = new String(event.getTitle());
                        resultBuff.append(String.valueOf(event.getEventId()) + "\t" + title + "\n");
                    }
                }
            } else {
                System.out.println("service is null");
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        String result = resultBuff.toString();
        return events == null ? "FAILED" : result;
    }
}
