package hu.infokristaly.front.manager;

import hu.exprog.beecomposit.front.manager.LocaleManager;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.domain.SubjectType;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;
import hu.infokristaly.middle.service.SubjectService;

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

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@SessionScoped
public class SubjectManager extends BasicManager<Subject> implements Serializable {

	private static final long serialVersionUID = -8935780475418079295L;

	@Inject
	private Logger logger;

	@Inject
	private SubjectService subjectService;

	@Inject
	private LocaleManager localeManager;

	public SubjectManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initModel();
		initValue();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || current.get().getId() != null) {
			current = Optional.of(new Subject());
		}
		setControlData(getCurrent());
	}

	public void handleReturn(SelectEvent event) {
		if (event.getObject() instanceof SubjectType) {
			SubjectType selected = (SubjectType) event.getObject();
			formModel.getControls().stream()
					.filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("subjectType"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		} 
	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					subjectService.persist(current.get());
				} else {
					subjectService.merge(current.get());
				}
			}
			current = Optional.of(new Subject());
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
	protected BasicService<Subject> getService() {
		return subjectService;
	}

	@Override
	protected Locale getLocale() {
		// TODO Auto-generated method stub
		return localeManager.getLocale();
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		Object result = null;
		if (model.getPropertyName().equals("subjectType")) {
			Long id = Long.valueOf((String) model.getValue());
			result = subjectService.findSubjectType(id);
		}
		return result;
	}

	public List<SubjectType> getSubjectType() {
		List<SubjectType> subjectTypes = subjectService.findAllSubjectType();
		return subjectTypes;
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
	public boolean checkDeleteRight(Subject entity)
			throws hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean checkEditableRights(Subject entity)
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

	@Override
	public boolean checkDetailsInTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkListRight() throws ActionAccessDeniedException {
		// TODO Auto-generated method stub
		return false;
	}

}
