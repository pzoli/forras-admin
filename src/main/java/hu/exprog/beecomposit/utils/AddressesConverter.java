package hu.exprog.beecomposit.utils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import hu.exprog.beecomposit.back.model.Addresses;
import hu.exprog.beecomposit.middle.service.AddressesService;

@FacesConverter("addressesConverter")
public class AddressesConverter implements Converter {

	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				InitialContext ic = new InitialContext();
				Object addressesService = ic.lookup("java:global/Homework4Telekocsi/AddressesService");
				if ((addressesService != null)) {
					Addresses result = ((AddressesService)addressesService).find(Addresses.class, Long.parseLong(value));
					return result;
				} else {
					return null;
				}
			} catch (NumberFormatException | NamingException e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
			}
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Addresses) object).getId());
		} else {
			return null;
		}
	}
}
