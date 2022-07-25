package hu.infokristaly.front.controller;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionBasedController {

    protected boolean initialized;
    
    public boolean isInitializedController(String conrollerName) {
        Object session = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session instanceof HttpSession) {
            String initializedController = (String) ((HttpSession) session).getAttribute("initializedController");
            return ((initializedController != null) && initializedController.equals(conrollerName));
        } else {
            return false;
        }
    }

    public static boolean isAjax() {
        Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
            return "XMLHttpRequest".equals(header);
        } else {
            return false;
        }
    }
    
    public void setInitializedController(String controllerName) {
        Object session = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session instanceof HttpSession) {
            ((HttpSession) session).setAttribute("initializedController", controllerName);
        }
    }
}
