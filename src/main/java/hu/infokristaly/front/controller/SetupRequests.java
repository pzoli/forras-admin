package hu.infokristaly.front.controller;

import hu.infokristaly.front.manager.SelfManager;
import hu.infokristaly.middle.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named
public class SetupRequests {

    @Inject
    private SelfManager selfManager;
    
    @Inject
    private UserService userService;
    
    public void updateSetupManager() {
        selfManager.setCurrentSystemUser(userService.getLoggedInSystemUser());
    }
}
