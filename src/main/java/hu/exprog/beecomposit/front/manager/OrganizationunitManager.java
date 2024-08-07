package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import org.primefaces.event.SelectEvent;

import hu.exprog.beecomposit.back.model.Addresses;
import hu.exprog.beecomposit.back.model.Organization;
import hu.exprog.beecomposit.back.model.Organizationunit;
import hu.exprog.beecomposit.middle.service.AddressesService;
import hu.exprog.beecomposit.middle.service.OrganizationService;
import hu.exprog.beecomposit.middle.service.OrganizationunitService;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@WindowScoped
public class OrganizationunitManager extends BasicManager<Organizationunit> implements Serializable {

	private static final long serialVersionUID = -4017487278734716247L;

	@Inject
	private Logger logger;

	@Inject
	private LocaleManager localeManager;

	@Inject
	private OrganizationunitService organizationunitService;

	@Inject
	private OrganizationService organizationService;

	private Organizationunit organization;

	private List<Addresses> selectedaddresses;

	@Inject
	private AddressesService addressesService;

	public OrganizationunitManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
		initModel();
		organization = new Organizationunit();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || (current.get().getId() != null)) {
			current = Optional.of(new Organizationunit());
			clearProperties();
		}
	}
		
	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					organizationunitService.merge(current.get());
				} else {
					organizationunitService.merge(current.get());
				}
			}
			current = Optional.of(new Organizationunit());
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
	protected BasicService<Organizationunit> getService() {
		return organizationunitService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		Object result = model.getValue();
		if ("organization".equals(model.getPropertyName())) {
			result = organizationService.find(Organization.class, Long.parseLong((String) model.getValue()));
		} else if ("addresses".equals(model.getPropertyName())) {
			Object value = model.getValue();
			LinkedList<Addresses> entries = new LinkedList<Addresses>();
			if (value != null) {
				Arrays.asList((Object[])value).stream().forEach(a -> {
					if (a instanceof Addresses) {
						Addresses found = addressesService.find((Addresses) a);
						entries.add(found);
					}
				});
			}
			result = entries;
		}
		return result;
	}

	public List<Organization> getOrganizations() {
		return organizationService.findAll();
	}

	public void handleReturn(SelectEvent event) {
		Organization selected = (Organization) event.getObject();
		if (current != null) {
			formModel.getControls().stream().filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("organization")).forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
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
		return value;
	}

	@Override
	public Object preProcess(FieldModel field, Object value) {
		return value;
	}

	public List<Addresses> getAddresses() {
		return addressesService.findAll();
	}

	public List<Addresses> getSelectedaddresses() {
		return selectedaddresses;
	}

	public void setSelectedaddresses(List<Addresses> selectedaddresses) {
		this.selectedaddresses = selectedaddresses;
	}

	public Organizationunit getOrganization() {
		return organization;
	}

	public void setOrganization(Organizationunit organization) {
		this.organization = organization;
	}

	@Override
	public boolean checkEditableRights(Organizationunit entity) throws ActionAccessDeniedException {
		return true;
	}

	@Override
	public boolean checkDeleteRight(Organizationunit entity) throws ActionAccessDeniedException {
		return true;
	}

	@Override
	public boolean checkDetailsInTable() {
		// TODO Auto-generated method stub
		return false;
	}

}
