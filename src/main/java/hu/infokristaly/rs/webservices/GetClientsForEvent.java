package hu.infokristaly.rs.webservices;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.middle.service.ScheduleService;

@Path("rest/getEventClients")
public class GetClientsForEvent {

	@GET()
	@Produces("text/plain; charset=UTF-8")
	public String getEventClients(@QueryParam("eventId") String eventId) {
		InitialContext ic;
		List<Client> clients = null;
		StringBuffer resultBuff = new StringBuffer();
		try {
			ic = new InitialContext();
			Object scheduleService = ic.lookup("java:/global/forras-admin/ScheduleService");
			if ((scheduleService != null) && (scheduleService instanceof ScheduleService)) {
				EventHistory eventHistory = new EventHistory();
				eventHistory.setEventId(Long.parseLong(eventId));
				eventHistory = ((ScheduleService) scheduleService).findEvent(eventHistory);
				clients = eventHistory.getClients();
				if (clients != null) {
					clients.forEach(c -> {
						resultBuff.append(c.getNyilvantartasiSzam() + "\t" + c.getNeve() + "\n");
					});
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		String result = resultBuff.toString();
		return clients == null ? "FAILED" : result;
	}

}
