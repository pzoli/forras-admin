package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.RFIDCard;
import hu.infokristaly.front.exceptions.ActionAccessDeniedException;
import hu.infokristaly.middle.service.BasicService;
import hu.infokristaly.middle.service.RFIDCardService;
import hu.infokristaly.utils.LookupFieldModel;

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
	protected Object getDetailFieldValue(LookupFieldModel model) {
		return null;
	}

	@Override
	protected Locale getLocale() {
		// TODO Auto-generated method stub
		return Locale.forLanguageTag("hu");
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

}