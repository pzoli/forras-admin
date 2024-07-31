package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

import org.primefaces.PrimeFaces;

import hu.exprog.beecomposit.back.model.AlertAddresses;
import hu.exprog.beecomposit.middle.service.AlertAddressesService;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@SessionScoped
public class AlertAddressesManager extends BasicManager<AlertAddresses> implements Serializable {

	private static final long serialVersionUID = -8823759600713604761L;

	@Inject
	private Logger logger;

	@Inject
	private LocaleManager localeManager;

	@Inject
	private AlertAddressesService alertAddressesService;

	protected Logger getLog() {
		return logger;
	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initModel();
		initValue();
	}

	public void initValue() {
		current = Optional.of(new AlertAddresses());
		clearProperties();
	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					alertAddressesService.persist(current.get());
				} else {
					alertAddressesService.merge(current.get());
				}
			}
			current = Optional.of(new AlertAddresses());
			clearProperties();
			PrimeFaces.current().ajax().addCallbackParam(VALIDATION_FAULT, false);
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
	protected BasicService<AlertAddresses> getService() {
		return alertAddressesService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		// TODO Auto-generated method stub
		return null;
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
		return value;
	}

	@Override
	public Object preProcess(FieldModel field, Object value) {
		return value;
	}

	@Override
	public boolean checkEditableRights(AlertAddresses entity) throws ActionAccessDeniedException {
		return true;
	}

	@Override
	public boolean checkDeleteRight(AlertAddresses entity) throws ActionAccessDeniedException {
		return true;
	}

	@Override
	public boolean checkDetailsInTable() {
		// TODO Auto-generated method stub
		return false;
	}

}
