package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import hu.exprog.beecomposit.back.application.ApplicationFunctions;
import hu.exprog.beecomposit.back.model.Language;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.model.Usergroup;
import hu.exprog.beecomposit.back.resources.AuthBackingBean;
import hu.exprog.beecomposit.middle.service.LanguageService;
import hu.exprog.beecomposit.middle.service.SystemUserService;
import hu.exprog.beecomposit.middle.service.UserGroupService;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@WindowScoped
public class SystemUserManager extends BasicManager<SystemUser> implements Serializable {

	private static final long serialVersionUID = 3610035839462161738L;

	@Inject
	private Logger logger;

	@Inject
	private LocaleManager localeManager;

	@Inject
	private SystemUserService userService;

	@Inject
	private UserGroupService userGroupService;

	@Inject
	private LanguageService languageService;

	@Inject
	private AuthBackingBean authBackingBean;

	@Inject
	private ApplicationFunctions applicationFunctions;

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initModel();
		initValue();
	}

	public void onEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Felhasználó adatai átszerkesztve", ((SystemUser) event.getObject()).getUserName());
		userService.merge((SystemUser) event.getObject());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Figyelmeztetés módosítása visszavonva", ((SystemUser) event.getObject()).getUserName());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void initValue() {
		current = Optional.of(new SystemUser());
		clearProperties();
	}

	public List<SystemUser> getUserList() {
		return userService.findAll(true);
	}

	public void save() throws ActionAccessDeniedException {
		if (!checkEditableRights(current.get()) && !checkSaveRight()) {
			throw new ActionAccessDeniedException(evalELToString("#{msg['access-denied']}"));
		}
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					authBackingBean.create(current.get());
				} else {
					authBackingBean.update(current.get());
				}
			}
			SystemUser loggedInUser = authBackingBean.getLoggedInUser();
			if ((loggedInUser != null) && current.get().getId().equals(loggedInUser.getId())) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.getELContext().setLocale(localeManager.getLocale());
				ResourceBundle.clearCache();
				applicationFunctions.clearBundle();
				initModel();
			} else {
				current = Optional.of(new SystemUser());
				clearProperties();
				PrimeFaces.current().ajax().addCallbackParam(VALIDATION_FAULT, false);
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
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected BasicService<SystemUser> getService() {
		return userService;
	}

	public List<Usergroup> getUsergroup() {
		List<Usergroup> result = userGroupService.findAll();
		if (!authBackingBean.checkDeveloperRights()) {
			result = result.stream().filter(g -> !g.getRolegroup().equalsIgnoreCase("ROLE_DEVELOPER")).collect(Collectors.toList());
		}
		return result;
	}

	public List<Language> getLanguage() {
		return languageService.findAll();
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		Object result = null;
		if ("usergroup".equals(model.getPropertyName())) {
			result = userGroupService.find(Usergroup.class, Long.parseLong((String) model.getValue()));
		} else if ("language".equals(model.getPropertyName())) {
			result = languageService.find(Language.class, Long.parseLong((String) model.getValue()));
		}
		return result;
	}

	public void handleReturn(SelectEvent event) {
		Object obj = event.getObject();
		if (obj instanceof Usergroup) {
			Usergroup selected = (Usergroup) obj;
			if (current.isPresent()) {
				formModel.getControls().stream().filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("usergroup")).forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
			}
		} else if (obj instanceof Language) {
			Language selected = (Language) obj;
			if (current.isPresent()) {
				formModel.getControls().stream().filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("language")).forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
			}
		}
	}

	@Override
	protected Locale getLocale() {
		return localeManager.getLocale();
	}

	@Override
	public boolean checkListRight() throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkSaveRight() throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkDeleteRight() throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object postProcess(FieldModel field, Object value) {
		Object result = value;
		if (field.getPropertyName().toLowerCase().equals("osuserpassword")) {
			if (current.get().getId() != null && (value == null || (value instanceof String && ((String) value).isEmpty()))) {
				SystemUser oldUser = userService.find(getCurrent());
				result = oldUser.getOsUserPassword();
			}
			if (value != null && value instanceof String && !((String) value).isEmpty()) {
				result = authBackingBean.getUserPassword((String) value);
			}
		}
		return result;
	}

	@Override
	public Object preProcess(FieldModel field, Object value) {
		return value;
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

	public void setCurrentToLoggedIn() {
		if (!isAjax()) {
			setCurrent(authBackingBean.getLoggedInUser());
			setControlData(current.get());
		}
	}

	@Override
	public boolean checkEditableRights(SystemUser entity) throws ActionAccessDeniedException {
		Usergroup developer = userGroupService.findUserRoleGroup("ROLE_DEVELOPER");
		return !entity.getUsergroup().equals(developer) || authBackingBean.checkDeveloperRights();
	}

	@Override
	public boolean checkDeleteRight(SystemUser entity) throws ActionAccessDeniedException {
		return checkEditableRights(entity);
	}

}
