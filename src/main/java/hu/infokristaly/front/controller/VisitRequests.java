package hu.infokristaly.front.controller;

import hu.infokristaly.front.manager.ActivityManager;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named
public class VisitRequests extends SessionBasedController {
    
    @Inject
    private ActivityManager activityManager;

    @PostConstruct
    public void init() {
        initialized = true;
    }
    
    public void updateManager(String view) {
        boolean isAjax = isAjax();
        boolean isInitializedController = isInitializedController(getClass().getSimpleName()+view);
        if (initialized && !isAjax && !isInitializedController) {
            setInitializedController(getClass().getSimpleName()+view);
            activityManager.setLazyDataModel(null);
            activityManager.setCurrentClientId(null);
            activityManager.setVisitorsTime(null);
            activityManager.setFilterNySzam(null);
            activityManager.setCurrentClient(null);
            activityManager.setCurrentTime(null);
        }
    }
}
