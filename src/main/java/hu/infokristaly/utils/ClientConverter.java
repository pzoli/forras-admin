package hu.infokristaly.utils;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.middle.service.ClientsService;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;

@FacesConverter(value="clientConv")
public class ClientConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value == null) {
			return null;
		} else {
			Client result = null;
			try {
				Long id = Long.valueOf(value);
				Client client = new Client();
				client.setId(id);
				InitialContext ic = new InitialContext();
				Object service = ic
						.lookup("java:/global/forras-admin/ClientsService");
				if ((service != null) && (service instanceof ClientsService)) {
					System.out.println("service:" + service);
					result = ((ClientsService) service).find(client);
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
			return String.valueOf(((Client) value).getId());
		}
	}

}
