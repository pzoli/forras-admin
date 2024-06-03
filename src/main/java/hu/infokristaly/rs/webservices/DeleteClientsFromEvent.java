package hu.infokristaly.rs.webservices;

import java.util.List;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.middle.service.UserService;

@Path("rest/deleteClientFromEvent")
public class DeleteClientsFromEvent {

	@GET()
	@Produces("text/plain; charset=UTF-8")
	public String deleteEventClients(@QueryParam("pin") String pin, @QueryParam("eventid") String eventId,@QueryParam("clientid") String clientid) {
		InitialContext ic;
		List<Client> clients = null;
		StringBuffer resultBuff = new StringBuffer();
		try {
			ic = new InitialContext();
			SystemUser user = null;
			Object userService = ic.lookup("java:/global/forras-admin/UserService");
			if ((userService != null) && (userService instanceof UserService)) {
				user = ((UserService)userService).findByPIN(pin);
				if (user == null) {
					return "PIN NOT FOUND";
				}
			}
			Object scheduleService = ic.lookup("java:/global/forras-admin/ScheduleService");
			if ((scheduleService != null) && (scheduleService instanceof ScheduleService)) {
				EventHistory eventHistory = new EventHistory();
				eventHistory.setEventId(Long.parseLong(eventId));
				eventHistory = ((ScheduleService) scheduleService).findEvent(eventHistory);
				if (!eventHistory.getLeaders().contains(user)) {
					return "NOT PERMITTED"; 
				}
				clients = eventHistory.getClients();
				if (clients != null) {
					List<Client> updatedClients = clients.stream().filter(c ->
						!c.getNyilvantartasiSzam().equals(clientid)
					).collect(Collectors.toList());
					eventHistory.setClients(updatedClients);
					((ScheduleService) scheduleService).persistEvent(eventHistory);
				}
				
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		String result = resultBuff.toString();
		return clients == null ? "FAILED" : result;
	}


}
