package hu.infokristaly.utils;

import hu.infokristaly.back.domain.SubjectType;
import hu.infokristaly.middle.service.SubjectService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="subjTypeConv")
public class SubjectTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            SubjectType result = null;
            try {
                Long id = Long.parseLong(value);
                InitialContext ic = new InitialContext();
                Object service = ic.lookup("java:/global/forras-admin/SubjectService");
                if ((service != null) && (service instanceof SubjectService)) {
                    System.out.println("service:" + service);
                    result = ((SubjectService) service).findSubjectType(id);
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
        if (value instanceof SubjectType) {
            SubjectType subjectType = (SubjectType) value;
            if (subjectType != null)
                return String.valueOf(subjectType.getId());
            else
                return null;
        } else {
            return String.valueOf(value);
        }
    }

}
