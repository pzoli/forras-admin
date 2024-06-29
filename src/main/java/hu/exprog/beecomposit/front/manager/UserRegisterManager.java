package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.model.UserRegister;
import hu.exprog.beecomposit.back.resources.AuthBackingBean;
import hu.exprog.beecomposit.middle.service.SystemUserService;
import hu.exprog.beecomposit.middle.service.UserRegisterService;
import hu.exprog.honeyweb.utils.JSFUtil;

@Named
@WindowScoped
public class UserRegisterManager implements Serializable {

	private static final long serialVersionUID = 3610035839462161738L;

	@Inject
	private Logger logger;

	@Inject
	private UserRegisterService userRegisterService;

	@Inject
	private SystemUserService systemUserService;

	@Inject
	private AuthBackingBean auth;

	@Inject
	private LocaleManager localeManager;

	private Optional<UserRegister> current;
	

	private String VALIDATION_FAULT = "validationFailed";

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
	}

	public void onEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Regisztrációs adatok átszerkesztve", ((UserRegister) event.getObject()).getUserName());
		userRegisterService.merge((UserRegister) event.getObject());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Regisztráció módosítása visszavonva", ((UserRegister) event.getObject()).getUserName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void initValue() {
		current = Optional.of(new UserRegister());
	}

	public UserRegister getCurrent() {
		return current.get();
	}

	public void setCurrent(UserRegister current) {
		this.current = Optional.of(current);
	}

	public String save() {
		String result = null;
		try {
			if (current.isPresent()) {
				SystemUser activeSystemUser = systemUserService.findByOsUserName(current.get().getEmailAddress());
				if (activeSystemUser != null) {
					FacesMessage message = new FacesMessage(JSFUtil.evalELToString("#{msg['userExists']}"));
					FacesContext.getCurrentInstance().addMessage(null, message);
				} else {
					String passwd = auth.getUserPassword(current.get().getUserPassword());
					current.get().setUserPassword(passwd);
					userRegisterService.merge(current.get());
					current = Optional.of(new UserRegister());
					initValue();
					PrimeFaces.current().ajax().addCallbackParam(VALIDATION_FAULT, false);
					result = "registration-success.xhtml?faces-redirect=true";
				}
			}
		} catch (EJBException e) {
			Throwable ex = e.getCause();
			if (ex instanceof ConstraintViolationException) {
				Set<ConstraintViolation<?>> msg = ((ConstraintViolationException) ex).getConstraintViolations();
				msg.forEach(c -> {
					StringBuffer validationExcMsg = new StringBuffer();
					ConstraintDescriptor<?> desc = c.getConstraintDescriptor();
					logger.info(desc.getAttributes().get("message").toString());
					String temp = c.getMessageTemplate();
					logger.info(temp);
					validationExcMsg.append(c.getPropertyPath()).append(":").append(c.getMessage());
					FacesMessage message = new FacesMessage(validationExcMsg.toString());
					FacesContext.getCurrentInstance().addMessage(null, message);
				});
			} else {
				FacesMessage message = new FacesMessage("Failed: " + e.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, message);
			}
			PrimeFaces.current().ajax().addCallbackParam(VALIDATION_FAULT, true);
		}
		return result;
	}

	public void activateUser(String hashId) {
		if (hashId != null) {
			UserRegister userRegister = userRegisterService.findByHashId(hashId);
			if (userRegister != null) {
				SystemUser user = systemUserService.createUserFromRegistration(userRegister);
				userRegister.setRegisteredUser(user);
				userRegister.setActivationDate(new Date());
				userRegisterService.merge(userRegister);
			}
		}

	}

}
