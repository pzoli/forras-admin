package hu.exprog.beecomposit.back.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SubjectInfo;
import org.jboss.security.identity.RoleGroup;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.model.UserJoinGroup;
import hu.exprog.beecomposit.back.model.UserJoinGroupId;
import hu.exprog.beecomposit.middle.service.SystemUserService;
import hu.infokristaly.back.model.AppProperties;
import hu.infokristaly.middle.service.UserService;

@Named
@RequestScoped
public class AuthBackingBean {

	@Inject
	private SystemUserService userService;
	private SystemUser loggedInUser = null;
	@Inject
	private AppProperties appProperties;

	public void logout() {
		String result = appProperties.getDefaultPage();

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		if (request.getUserPrincipal() != null) {
			try {
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
				request.logout();
				session.invalidate();
				String url = context.getApplication().getViewHandler().getActionURL(context, result);
				context.getExternalContext().redirect(url);
			} catch (ServletException e) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception",
						"Exception occurred at logout. " + e.getLocalizedMessage()));
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

	public Principal getLoggedInPrincipal() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		Principal principal = request.getUserPrincipal();
		if (principal != null && loggedInUser == null) {
			loggedInUser = userService.findByOsUserName(principal.getName());
		}
		return principal;
	}

	public String getPrincipalRoles() {
		SubjectInfo info = SecurityContextAssociation.getSecurityContext().getSubjectInfo();
		RoleGroup roles = info.getRoles();
		String result = null;
		if (roles != null) {
			result = roles.getRoles().toString();
		}
		return result;
	}

	public boolean checkAdminRights() {
		Optional<String> rights = Optional.ofNullable(getPrincipalRoles());
		boolean result = rights.isPresent()
				&& (rights.get().indexOf("ROLE_ADMIN") > -1 || rights.get().indexOf("ROLE_DEVELOPER") > -1);
		return result;
	}

	public boolean checkDeveloperRights() {
		Optional<String> rights = Optional.ofNullable(getPrincipalRoles());
		boolean result = rights.isPresent() && (rights.get().indexOf("ROLE_DEVELOPER") > -1);
		return result;
	}

	public boolean isEditorReadOnly() {
		return !checkDeveloperRights();
	}

	public void create(SystemUser user) {
		String SHA256pass = "";
		try {
			SHA256pass = Base64.encodeBase64String(toSHA256(user.getOsUserPassword().getBytes("UTF-8")));
			user.setEnabled(true);
			user.setOsUserPassword(SHA256pass);
			userService.persist(user);
			UserJoinGroupId userJoinGroupId = new UserJoinGroupId();
			if (user.isAdminUser()) {
				userJoinGroupId.setGroupName(UserService.ADMIN_GROUP);
			} else {
				userJoinGroupId.setGroupName(UserService.USER_GROUP);
			}
			userJoinGroupId.setUserName(user.getOsUserName());
			UserJoinGroup userJoinGroup = new UserJoinGroup();
			userJoinGroup.setUserJoinGroupId(userJoinGroupId);
			userService.createRole(userJoinGroup);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("SHA-256/Base64:" + SHA256pass);
	}

	public String getUserPassword(SystemUser systemUser) {
		return getUserPassword(systemUser.getOsUserPassword());
	}

	public String getUserPassword(String systemUserPassword) {
		String SHA256pass = "";
		try {
			SHA256pass = Base64.encodeBase64String(toSHA256(systemUserPassword.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// System.out.println("SHA-256/Base64:" + SHA256pass);
		return SHA256pass;
	}

	public void update(SystemUser user) {
		user = userService.merge(user);
		if (user.getUsergroup() != null) {
			userService.removeRoles(user);
			UserJoinGroupId userJoinGroupId = new UserJoinGroupId();
			userJoinGroupId.setGroupName(user.getUsergroup().getRolegroup());
			userJoinGroupId.setUserName(user.getOsUserName());
			UserJoinGroup userJoinGroup = new UserJoinGroup();
			userJoinGroup.setUserJoinGroupId(userJoinGroupId);
			userService.createRole(userJoinGroup);
		}
	}

	public SystemUser getLoggedInUser() {
		if (loggedInUser == null) {
			getLoggedInPrincipal();
		}
		return loggedInUser;
	}
}
