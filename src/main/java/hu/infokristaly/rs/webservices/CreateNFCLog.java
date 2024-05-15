package hu.infokristaly.rs.webservices;

import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.RFIDCard;
import hu.infokristaly.back.domain.RFIDCardReader;
import hu.infokristaly.back.domain.RFIDCardUser;
import hu.infokristaly.back.domain.RFIDLogEntry;
import hu.infokristaly.middle.service.RFIDCardReadersService;
import hu.infokristaly.middle.service.RFIDCardService;
import hu.infokristaly.middle.service.RFIDCardUserService;
import hu.infokristaly.middle.service.RFIDLogEntryService;

@Path("rest/createNFCLog")
public class CreateNFCLog {

    @GET()
    @Produces("text/plain")
    public String createNFCLog(@QueryParam("rfid") String rfid, @QueryParam("type") String type, @QueryParam("readerid") String readerid, @QueryParam("serverid") String serverid) {
        InitialContext ic;
        String result = "OK";
        try {
            ic = new InitialContext();
            Object rFIDLogEntryService = ic.lookup("java:/global/forras-admin/RFIDLogEntryService");
            Object rFIDCardReadersService = ic.lookup("java:/global/forras-admin/RFIDCardReadersService");
            Object rFIDCardService = ic.lookup("java:/global/forras-admin/RFIDCardService");
            Object rFIDCardUserService = ic.lookup("java:/global/forras-admin/RFIDCardUserService");
            if ((rFIDLogEntryService != null) && (rFIDLogEntryService instanceof RFIDLogEntryService) && (rFIDCardService != null) && (rFIDCardService instanceof RFIDCardService) && (rFIDCardUserService != null) && (rFIDCardUserService instanceof RFIDCardUserService) && (rFIDCardReadersService != null) && (rFIDCardReadersService instanceof RFIDCardReadersService)) {
                System.out.println("service:" + rFIDLogEntryService);
                RFIDCard rfidCard = new RFIDCard();
                rfidCard.setRfid(rfid);
                rfidCard.setType(type);
                rfidCard = ((RFIDCardService) rFIDCardService).findByRFID(rfidCard);
                if (rfidCard != null) {
                    RFIDCardReader rfidCardReader = new RFIDCardReader();
                    rfidCardReader.setReaderId(readerid);
                    rfidCardReader = ((RFIDCardReadersService) rFIDCardReadersService).findByReaderId(rfidCardReader);
                    if (rfidCardReader != null) {
                        RFIDCardUser rfidCardUser = new RFIDCardUser();
                        rfidCardUser.setRfidCard(rfidCard);
                        rfidCardUser = ((RFIDCardUserService) rFIDCardUserService).findActiveByRfidCard(rfidCard, new Date());
                        if (rfidCardUser != null) {
                            RFIDLogEntry logEntry = new RFIDLogEntry();
                            logEntry.setLogDate(new Date());
                            logEntry.setRfidCardReader(rfidCardReader);
                            logEntry.setRfidCardUser(rfidCardUser);
                            ((RFIDLogEntryService) rFIDLogEntryService).persist(logEntry);
                        }
                    }
                }
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
