package hu.exprog.beecomposit.back.application;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;

import hu.exprog.beecomposit.middle.service.AlertAddressesService;

@Named
@ApplicationScoped
public class ApplicationFunctions implements Serializable {

	private static final long serialVersionUID = 4626038317940810045L;

	private List<Thread> runnables = new LinkedList<Thread>();

	@Inject
	private AlertAddressesService alertAddressesService;

	public List<Thread> getRunnables() {
		return runnables;
	}

	@PostConstruct
	private void init() {
	}

	public void clearBundle() {
		ResourceBundle.clearCache();
		Map<String,ApplicationResourceBundle> bundles = ApplicationAssociate.getCurrentInstance().getResourceBundles();
		ApplicationResourceBundle applicationBundle = bundles.get("msg");
		try {
			Field field = applicationBundle.getClass().getDeclaredField("resources");
			field.setAccessible(true);
			Map<Locale, ResourceBundle> resources = (Map<Locale, ResourceBundle>) field.get(applicationBundle);
			resources.clear();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
