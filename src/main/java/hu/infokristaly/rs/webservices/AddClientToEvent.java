package hu.infokristaly.rs.webservices;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.middle.service.ScheduleService;

@Path("rest/addClientToEvent")
public class AddClientToEvent {

    @GET()
    @Produces("text/plain")
    public String addClientToEvent(@QueryParam("pin") String pinCode, @QueryParam("eventid") Long eventId, @QueryParam("clientid") String clientId) {
        InitialContext ic;
        EventHistory event = null;
        String result = "OK";
        try {
            ic = new InitialContext();
            Object scheduleService = ic.lookup("java:/global/forras-admin/ScheduleService");
            if ((scheduleService != null) && (scheduleService instanceof ScheduleService)) {
                System.out.println("service:" + scheduleService);
                result = ((ScheduleService) scheduleService).addUserToEvent(eventId,clientId,pinCode);
            } else {
                result = "FAILED: Service is null";
            }
        } catch (NamingException e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }
}
