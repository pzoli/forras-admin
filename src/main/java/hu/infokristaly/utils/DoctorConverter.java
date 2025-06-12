package hu.infokristaly.utils;

import hu.infokristaly.back.domain.Doctor;
import hu.infokristaly.middle.service.DoctorsService;
import hu.infokristaly.middle.service.UserService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
@FacesConverter(value="docConv")
public class DoctorConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            Doctor result = null;
            try {
                Doctor doc = new Doctor(Long.parseLong(value));
                InitialContext ic = new InitialContext();
                Object service = ic.lookup("java:/global/forras-admin/DoctorsService");
                if ((service != null) && (service instanceof UserService)) {
                    System.out.println("service:" + service);
                    result = ((DoctorsService) service).find(doc);
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
        if (value == null) {
            return null;
        } else {
            return String.valueOf(((Doctor) value).getId());
        }
    }

}
