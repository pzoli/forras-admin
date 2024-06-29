package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.resources.AuthBackingBean;
import hu.exprog.honeyweb.front.manager.BasicManager;

@Named
@SessionScoped
public class LocaleManager implements Serializable {

	private static final long serialVersionUID = 4336680461821345561L;

	private Locale locale;

	@Inject
	private AuthBackingBean authBean;

	public static final String DEFAULT_LOCALE = "hu_HU";

	public LocaleManager() {
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		if (root != null) {
			locale = root.getLocale();
		} else {
			locale = new Locale(DEFAULT_LOCALE);
		}
	}

	public Locale getLocale() {
		if (authBean.getLoggedInPrincipal() != null) {
			SystemUser user = authBean.getLoggedInUser();
			if (user != null && user.getLanguage() != null && user.getLanguage().getLocaleCode() != null && !user.getLanguage().getLocaleCode().isEmpty()) {
				//Logger.getLogger(getClass()).info("origin getLocale lang:" + locale);
				Locale userLocale = new Locale(user.getLanguage().getLocaleCode());
				if (!userLocale.equals(locale)) {
					//Logger.getLogger(getClass()).info("new getLocale lang:" + locale.getLanguage());
					locale = userLocale; 
				}
			}
		}
		return locale;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	@SuppressWarnings("rawtypes")
	public void setLanguage(String language) {
		locale = new Locale(language);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
		for (Object o : sessionMap.values()) {
			if (o instanceof BasicManager) {
				((BasicManager) o).initModel();
			}
		}
	}

}
