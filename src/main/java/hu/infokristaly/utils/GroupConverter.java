package hu.infokristaly.utils;

import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.middle.service.GroupForClientsService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="groupConv")
public class GroupConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            GroupForClients result = null;
            try {
                Integer id = Integer.parseInt(value);
                InitialContext ic = new InitialContext();
                Object service = ic.lookup("java:/global/forras-admin/GroupForClientsService");
                if ((service != null) && (service instanceof GroupForClientsService)) {
                    System.out.println("service:" + service);
                    result = ((GroupForClientsService) service).find(id);
                } else {
                    System.out.println("service is null");
                }
            } catch (NumberFormatException ex) {

            } catch (Throwable t) {
                System.out.println("service lookup failed");
            }

            return result;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof GroupForClients) {
            GroupForClients group = (GroupForClients) value;
            if (group != null)
                return String.valueOf(group.getId());
            else
                return null;
        } else {
            return String.valueOf(value);
        }
    }

}
