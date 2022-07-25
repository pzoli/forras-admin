package hu.infokristaly.front.controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@RequestScoped
@Named
public class FormBasedLogin {

    private String action = "j_security_check";
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getAction() {
        return action;
    }
}
