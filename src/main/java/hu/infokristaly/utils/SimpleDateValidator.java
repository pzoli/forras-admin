package hu.infokristaly.utils;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;

@FacesValidator("simpleDateValidator")
public class SimpleDateValidator implements Validator, ClientValidator {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.mm.dd."); 
    
    public SimpleDateValidator() {
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "simpleDateValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nem lehet üres az esemény!",
                    value + " érvénytelen érték."));
        } else {                        
            context.addMessage(null, new FacesMessage("Dátum nem érvényes!"));
        }
    }

}
