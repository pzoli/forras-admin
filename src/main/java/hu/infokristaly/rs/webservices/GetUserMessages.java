package hu.infokristaly.rs.webservices;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import hu.infokristaly.back.domain.Message;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.UserService;

@Path("rest/userMessages")
public class GetUserMessages {

    @GET()
    @Path("getCount")
    @Produces("text/plain; charset=UTF-8")
    public String getUserMessageCount(@QueryParam("pin") String pinCode) {
        InitialContext ic;
        StringBuffer resultBuff = new StringBuffer();
        try {
            ic = new InitialContext();
            Integer count = 0;
            Object userService = ic.lookup("java:/global/forras-admin/UserService");
            Object messageService = ic.lookup("java:/global/forras-admin/MessageService");
            if ((userService != null)
                    && (messageService != null)) {
                System.out.println("service:" + userService);
                SystemUser iSystemUser = ((UserService) userService).findByPIN(pinCode);
                GregorianCalendar start = new GregorianCalendar();
                GregorianCalendar end = new GregorianCalendar();
                count = ((MessageService)messageService).findMessages(start.getTime(),end.getTime(), TimeZone.getDefault(),iSystemUser).size();
            } else {
                System.out.println("service is null");
            }
            resultBuff.append(count);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        String result = resultBuff.toString();
        return result.isEmpty() ? "FAILED" : result;
    }

    @GET()
    @Path("getMessages")
    @Produces("text/plain; charset=UTF-8")
    public String getUserMessages(@QueryParam("pin") String pinCode) {
        InitialContext ic;
        StringBuffer resultBuff = new StringBuffer();
        List<Message> iMessages = null;
        try {
            ic = new InitialContext();
            Object userService = ic.lookup("java:/global/forras-admin/UserService");
            Object messageService = ic.lookup("java:/global/forras-admin/MessageService");
            if ((userService != null) && (userService instanceof UserService)
                    && (messageService != null) && (messageService instanceof MessageService)) {
                System.out.println("service:" + userService);
                SystemUser iSystemUser = ((UserService)userService).findByPIN(pinCode);
                iMessages = ((MessageService)messageService).findMessages(iSystemUser);
                for (Message iMessage : iMessages) {
                    String content = new String(iMessage.getMessage());
                    resultBuff.append("<message id='" + String.valueOf(iMessage.getId()) + "'>" + content + "</message>");
                }
            } else {
                System.out.println("service is null");
            }
        } catch (NamingException ex) {

        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Messages_Table>" + resultBuff.toString() + "</Messages_Table>";
    }
    
    @GET()
    @Path("getMessageInterval")
    @Produces("text/plain; charset=UTF-8")
    public String getMessageInterval(@QueryParam("pin") String pinCode, @QueryParam("daystart") String daystart, @QueryParam("dayend") String dayend) {
        InitialContext ic;
        StringBuffer resultBuff = new StringBuffer();
        List<Message> iMessages = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate =  df.parse(daystart); 
            Date endDate =  df.parse(dayend);
            ic = new InitialContext();
            Object userService = ic.lookup("java:/global/forras-admin/UserService");
            Object messageService = ic.lookup("java:/global/forras-admin/MessageService");
            if ((userService != null) && (userService instanceof UserService)
                    && (messageService != null) && (messageService instanceof MessageService)) {
                System.out.println("service:" + userService);
                SystemUser iSystemUser = ((UserService)userService).findByPIN(pinCode);
                iMessages = ((MessageService)messageService).findMessages(startDate,endDate, TimeZone.getDefault(), iSystemUser);
                for (Message iMessage : iMessages) {
                    String content = new String(iMessage.getMessage());
                    resultBuff.append("<message id='" + String.valueOf(iMessage.getId()) + "'>" + content + "</message>");
                }
            } else {
                System.out.println("service is null");
            }
        } catch (NamingException | ParseException ex) {

        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Messages_Table>" + resultBuff.toString() + "</Messages_Table>";
    }
}
