package hu.infokristaly.front.manager;

import hu.exprog.beecomposit.front.manager.LocaleManager;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.infokristaly.back.domain.RFIDCard;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.infokristaly.middle.service.RFIDCardService;

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

import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@SessionScoped
public class RFIDCardManager extends BasicManager<RFIDCard> implements Serializable {

	private static final long serialVersionUID = -8935780475418079295L;

	@Inject
	private Logger logger;

	@Inject
	private RFIDCardService rFIDCardService;

	@Inject
	private LocaleManager localeManager;

	public RFIDCardManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
		initModel();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || current.get().getId() != null) {
			current = Optional.of(new RFIDCard());
		}
	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					rFIDCardService.persist(current.get());
				} else {
					rFIDCardService.merge(current.get());
				}
			}
			current = Optional.of(new RFIDCard());
			clearProperties();
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
		}
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected BasicService<RFIDCard> getService() {
		return rFIDCardService;
	}

	@Override
	protected Locale getLocale() {
		// TODO Auto-generated method stub
		return localeManager.getLocale();
	}

	@Override
	protected Object getDetailFieldValue(hu.exprog.honeyweb.utils.LookupFieldModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkListRight() throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkSaveRight() throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkDeleteRight() throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkDeleteRight(RFIDCard entity)
			throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkEditableRights(RFIDCard entity)
			throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object postProcess(FieldModel field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object preProcess(FieldModel field, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

}
