package hu.infokristaly.rs.webservices;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.middle.service.UserService;

@Path("rest/addClientToEvent")
public class AddClientToEvent {

	@GET()
	@Produces("text/plain")
	public String addClientToEvent(@QueryParam("pin") String pin, @QueryParam("eventid") Long eventId,
			@QueryParam("clientid") String clientId) {
		InitialContext ic;
		String result = "OK";
		try {
			ic = new InitialContext();
			SystemUser user = null;
			Object userService = ic.lookup("java:/global/forras-admin/UserService");
			if ((userService != null) && (userService instanceof UserService)) {
				user = ((UserService) userService).findByPIN(pin);
				if (user == null) {
					return "PIN NOT FOUND";
				}
			}
			Object scheduleService = ic.lookup("java:/global/forras-admin/ScheduleService");
			if ((scheduleService != null) && (scheduleService instanceof ScheduleService)) {
				System.out.println("service:" + scheduleService);
				EventHistory event = ((ScheduleService) scheduleService).find(eventId);
				event.setEventId(eventId);
				event = ((ScheduleService) scheduleService).findEvent(event);
				if (!event.getLeaders().contains(user)) {
					return "NOT PERMITTED";
				}
				result = ((ScheduleService) scheduleService).addUserToEvent(eventId, clientId, pin);
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
