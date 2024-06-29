package hu.infokristaly.front.view;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.UserService;

@Named
@RequestScoped
public class IdleMonitorView {

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    public void onIdle() {
        SystemUser systemUser = userService.getLoggedInSystemUser();
        if (systemUser != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Nincs aktivitás.", "Várakozok..."));
        }
    }

    public void onActive() {
        SystemUser systemUser = userService.getLoggedInSystemUser();
        FacesMessage message = null;
        if (systemUser != null) {
            Map<String, Object> actualfilters = new HashMap<String, Object>();
            actualfilters.put("systemUser", systemUser);
            int count = messageService.countUnreded(actualfilters);
            if (count > 0) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Újra aktivitás", "Kedves " + (systemUser.getUserName()) + "! Olvasatlan üzeneteid (" + count + ") vannak.");
            }
        }
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

}
