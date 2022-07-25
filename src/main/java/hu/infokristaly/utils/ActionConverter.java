package hu.infokristaly.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "ActionConv")
public class ActionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object result = null;
        if (value == null) {
            switch (value) {
            case "Létrehozás":
                result = 1;
                break;
            case "Törlés":
                result = 3;
                break;
            case "Futás":
                result = 4;
                break;
            default:
                result = 2;
            }
        }
        return result;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String result = null;
        if (value != null) {
            switch ((Byte) value) {
            case 1:
                result = "Létrehozás";
                break;
            case 3:
                result = "Törlés";
                break;
            case 4:
                result = "Futás";
                break;
            default:
                result = "Módosítás";
            }
        }
        return result;
    }

}
