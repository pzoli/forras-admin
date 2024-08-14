package hu.infokristaly.front.manager;

import java.io.Serializable;
import java.util.List;
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

import org.primefaces.event.SelectEvent;

import hu.infokristaly.back.domain.RFIDCardReader;
import hu.infokristaly.back.domain.RFIDCardUser;
import hu.infokristaly.back.domain.RFIDLogEntry;
import hu.exprog.beecomposit.front.manager.LocaleManager;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.infokristaly.middle.service.RFIDCardReadersService;
import hu.infokristaly.middle.service.RFIDCardUserService;
import hu.infokristaly.middle.service.RFIDLogEntryService;
import hu.infokristaly.middle.service.UserService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@SessionScoped
public class RFIDLogEntryManager extends BasicManager<RFIDLogEntry> implements Serializable {

	private static final long serialVersionUID = -8935780475418079295L;

	@Inject
	private Logger logger;

	@Inject
	private RFIDLogEntryService rFIDLogEntryService;

	@Inject
	private RFIDCardUserService rFIDCardUserService;

	@Inject
	private RFIDCardReadersService rFIDCardReadersService;

	@Inject
	private UserService userService;

	@Inject
	private LocaleManager localeManager;

	public RFIDLogEntryManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
		initModel();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || current.get().getId() != null) {
			current = Optional.of(new RFIDLogEntry());
			clearProperties();
		}
	}

	public void handleReturn(SelectEvent event) {
		if (event.getObject() instanceof RFIDCardUser) {
			RFIDCardUser selected = (RFIDCardUser) event.getObject();
			formModel.getControls().stream()
					.filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("rfidCardUser"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		} else if (event.getObject() instanceof RFIDCardReader) {
			RFIDCardReader selected = (RFIDCardReader) event.getObject();
			formModel.getControls().stream()
					.filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("rfidCardReader"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		}

	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					rFIDLogEntryService.persist(current.get());
				} else {
					rFIDLogEntryService.merge(current.get());
				}
			}
			current = Optional.of(new RFIDLogEntry());
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
	protected BasicService<RFIDLogEntry> getService() {
		return rFIDLogEntryService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		Object result = null;
		if (model.getPropertyName().equals("rfidCardUser")) {
			Long id = Long.valueOf((String) model.getValue());
			RFIDCardUser rfidCard = new RFIDCardUser();
			rfidCard.setId(id);
			result = rFIDCardUserService.find(rfidCard);
		} else if (model.getPropertyName().equals("rfidCardReader")) {
			Long id = Long.valueOf((String) model.getValue());
			RFIDCardReader rfidCardReader = new RFIDCardReader();
			rfidCardReader.setId(id);
			result = rFIDCardReadersService.find(rfidCardReader);
		}
		return result;
	}

	public List<RFIDCardUser> getRfidCardUser() {
		List<RFIDCardUser> rfidCard = rFIDCardUserService.findAll();
		return rfidCard;
	}

	public List<RFIDCardReader> getRfidCardReader() {
		List<RFIDCardReader> systemUser = rFIDCardReadersService.findAll();
		return systemUser;
	}

	@Override
	protected Locale getLocale() {
		// TODO Auto-generated method stub
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
	public boolean checkDeleteRight(RFIDLogEntry entity) throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkEditableRights(RFIDLogEntry entity) throws ActionAccessDeniedException {
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

	@Override
	public boolean checkDetailsInTable() {
		// TODO Auto-generated method stub
		return false;
	}

}
