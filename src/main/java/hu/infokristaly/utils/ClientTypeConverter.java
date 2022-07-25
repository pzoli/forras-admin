package hu.infokristaly.utils;

import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.middle.service.ClientsService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="clientTypeConv")
public class ClientTypeConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value == null || ((value!=null) && (value.isEmpty()))) {
		    ClientType result = new ClientType();
			return result;
		} else {
			ClientType result = null;
			try {
				Integer id = Integer.parseInt(value);
				ClientType clientType = new ClientType();
				clientType.setId(id);
				InitialContext ic = new InitialContext();
				Object service = ic
						.lookup("java:/global/forras-admin/ClientsService");
				if ((service != null) && (service instanceof ClientsService)) {
					System.out.println("service:" + service);
					result = ((ClientsService) service).findClientType(clientType);
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
			return String.valueOf(((ClientType) value).getId());
		}
	}

}
