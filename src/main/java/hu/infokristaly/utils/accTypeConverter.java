package hu.infokristaly.utils;

import hu.infokristaly.back.domain.AccessibleType;
import hu.infokristaly.middle.service.ClientsService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="accTypeConv")
public class accTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            AccessibleType result = null;
            try {
                Integer id = Integer.parseInt(value);
                InitialContext ic = new InitialContext();
                Object service = ic
                        .lookup("java:/global/forras-admin/ClientsService");
                if ((service != null) && (service instanceof ClientsService)) {
                    System.out.println("service:" + service);
                    result = ((ClientsService) service).findAccessibleType(id);
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
        AccessibleType accType = (AccessibleType) value;
        if (accType != null)
            return String.valueOf(accType.getId());
        else
            return null;
    }

}
