package hu.infokristaly.utils;

import hu.exprog.beecomposit.back.model.Language;
import hu.exprog.beecomposit.middle.service.LanguageService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="languageConv")
public class LanguageConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value == null || ((value!=null) && (value.isEmpty()))) {
			Language result = new Language();
			return result;
		} else {
			Language result = null;
			try {
				Long id = Long.parseLong(value);
				Language language = new Language();
				language.setId(id);
				InitialContext ic = new InitialContext();
				Object service = ic
						.lookup("java:/global/forras-admin/LanguageService");
				if ((service != null) && (service instanceof LanguageService)) {
					System.out.println("service:" + service);
					result = ((LanguageService) service).find(language);
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
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value == null) {
			return null;
		} else {
			return String.valueOf(((Language) value).getId());
		}
	}

}
