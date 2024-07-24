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

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.RFIDCard;
import hu.infokristaly.back.domain.RFIDCardUser;
import hu.exprog.beecomposit.front.manager.LocaleManager;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.RFIDCardService;
import hu.infokristaly.middle.service.RFIDCardUserService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@SessionScoped
public class RFIDCardUserManager extends BasicManager<RFIDCardUser> implements Serializable {

	private static final long serialVersionUID = -8935780475418079295L;

	@Inject
	private Logger logger;

	@Inject
	private RFIDCardUserService rFIDCardUserService;
	
	@Inject
	private RFIDCardService rFIDCardService;
	
	@Inject
	private ClientsService clientService;

	@Inject
	private LocaleManager localeManager;

	public RFIDCardUserManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
		initModel();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || current.get().getId() != null) {
			current = Optional.of(new RFIDCardUser());
		}
	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
				    rFIDCardUserService.persist(current.get());
				} else {
					rFIDCardUserService.merge(current.get());
				}
			}
			current = Optional.of(new RFIDCardUser());
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

	public void handleReturn(SelectEvent event) {
		if (event.getObject() instanceof RFIDCard) {
			RFIDCard selected = (RFIDCard) event.getObject();
			formModel.getControls().stream()
					.filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("rfidCard"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected BasicService<RFIDCardUser> getService() {
		return rFIDCardUserService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
	    Object result = null;
	    if (model.getPropertyName().equals("rfidCard")) {
	        Long id = Long.valueOf((String)model.getValue());
	        RFIDCard rfidCard = new RFIDCard();
	        rfidCard.setId(id);
	        result = rFIDCardService.find(rfidCard);
	    } else if (model.getPropertyName().equals("client")) {
	        Long id = Long.valueOf((String)model.getValue());
	        Client client = new Client();
	        client.setId(id);
	        result = clientService.find(client);
	    }
		return result;
	}

	public List<RFIDCard> getRfidCard() {
	    List<RFIDCard> rfidCard = rFIDCardService.findAll();
        return rfidCard;
    }

    public List<Client> getClient() {
        List<Client> systemUser = clientService.findAll(true, null, null,null);
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
	public boolean checkDeleteRight(RFIDCardUser entity) throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkEditableRights(RFIDCardUser entity) throws ActionAccessDeniedException {
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
