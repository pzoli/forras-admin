package hu.infokristaly.back.auth;

import hu.infokristaly.back.model.AppProperties;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.back.model.UserJoinGroup;
import hu.infokristaly.back.model.UserJoinGroupId;
import hu.infokristaly.middle.service.UserService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

@Named
@RequestScoped
public class AuthBackingBean {
	
	@Inject
	private UserService userService;
	
	@Inject
	private AppProperties appProperties;
	
    public void logout() {        

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        if (request.getUserPrincipal() != null) {
            try {
                HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                session.removeAttribute(UserService.LOGGED_IN_SYSTEM_USER);
            	request.logout();
                session.invalidate();
                String result = appProperties.getDefaultPage(); 
                String url = context.getApplication().getViewHandler().getActionURL(context, result);
                context.getExternalContext().redirect(url);
            } catch (ServletException e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception", "Exception occurred at logout. "+e.getLocalizedMessage()));
            } catch (IOException ex) {
            }
        }

    }
    
    public static byte[] toSHA256(byte[] convertme) {
        byte[] result = {};
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            result = md.digest(convertme);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void create(SystemUser user) {
    	String SHA256pass = "";
    	try {
    		SHA256pass = Base64.encodeBase64String(toSHA256(user.getUserpassword().getBytes("UTF-8")));
    		user.setEnabled(true);
    		user.setUserpassword(SHA256pass);
    		userService.createUser(user);
    		UserJoinGroupId userJoinGroupId = new UserJoinGroupId();
    		if (user.isAdminUser()) {
    		    userJoinGroupId.setGroupName(UserService.ADMIN_GROUP);
    		} else {
    		    userJoinGroupId.setGroupName(UserService.USER_GROUP);
    		}
    		userJoinGroupId.setUserName(user.getEmailAddress());
    		UserJoinGroup userJoinGroup = new UserJoinGroup();
    		userJoinGroup.setUserJoinGroupId(userJoinGroupId);
    		userService.createRole(userJoinGroup);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	System.out.println("SHA-256/Base64:"+SHA256pass);
    }

	public Principal getLoggedInPrincipal() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Principal principal = request.getUserPrincipal();
		return principal;
	}

    public String getUserPassword(SystemUser systemUser) {
        String SHA256pass = "";
        try {
            SHA256pass = Base64.encodeBase64String(toSHA256(systemUser.getUserpassword().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("SHA-256/Base64:"+SHA256pass);
        return SHA256pass;
    }
	
}
