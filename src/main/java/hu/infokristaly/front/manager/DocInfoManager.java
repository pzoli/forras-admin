package hu.infokristaly.front.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;

import hu.infokristaly.back.domain.DocInfo;
import hu.infokristaly.back.domain.DocumentSubject;
import hu.infokristaly.back.domain.FileInfo;
import hu.infokristaly.back.model.AppProperties;
import hu.exprog.beecomposit.back.model.Organization;
import hu.exprog.beecomposit.front.manager.LocaleManager;
import hu.exprog.beecomposit.middle.service.OrganizationService;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.infokristaly.middle.service.DocInfoService;
import hu.infokristaly.middle.service.DocumentSubjectService;
import hu.infokristaly.middle.service.FileInfoService;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;

@Named
@SessionScoped
public class DocInfoManager extends BasicManager<DocInfo> implements Serializable {

	private static final long serialVersionUID = -8935780475418079295L;

	@Inject
	private Logger logger;

	@Inject
	private DocInfoService docInfoService;

	@Inject
	private DocumentSubjectService documentSubjectService;

	@Inject
	private OrganizationService organizationService;

	@Inject
	private LocaleManager localeManager;

	@Inject
	private AppProperties appProperties;

	@Inject
	private FileInfoService fileInfoService;

	public DocInfoManager() {

	}

	@PostConstruct
	public void init() {
		logger.log(Level.INFO, "[" + this.getClass().getName() + "] constructor finished.");
		initValue();
		initModel();
	}

	public void initValue() {
		if ((current == null) || (!current.isPresent()) || current.get().getId() != null) {
			current = Optional.of(new DocInfo());
		}
	}

	public void save() {
		setCurrentBeanProperties();
		try {
			if (current.isPresent()) {
				if (current.get().getId() == null) {
					docInfoService.persist(current.get());
				} else {
					docInfoService.merge(current.get());
				}
			}
			current = Optional.of(new DocInfo());
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
		if (event.getObject() instanceof DocumentSubject) {
			DocumentSubject selected = (DocumentSubject) event.getObject();
			formModel.getControls().stream().filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("subject"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		} else if (event.getObject() instanceof Organization) {
			Organization selected = (Organization) event.getObject();
			formModel.getControls().stream()
					.filter(c -> ((FieldModel) c.getData()).getPropertyName().equals("organization"))
					.forEach(d -> setDetailFieldValue((LookupFieldModel) d.getData(), selected.getId()));
		}
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected BasicService<DocInfo> getService() {
		return docInfoService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		Object result = null;
		if (model.getPropertyName().equals("subject")) {
			Long id = Long.valueOf((String) model.getValue());
			DocumentSubject subject = new DocumentSubject();
			subject.setId(id);
			result = documentSubjectService.find(subject);
		} else if (model.getPropertyName().equals("organization")) {
			Long id = Long.valueOf((String) model.getValue());
			Organization organization = new Organization();
			organization.setId(id);
			result = organizationService.find(organization);
		}
		return result;
	}

	@Override
	protected Locale getLocale() {
		return localeManager.getLocale();
	}

	public List<DocumentSubject> getSubject() {
		List<DocumentSubject> subject = documentSubjectService.findAll();
		return subject;
	}

	public List<Organization> getOrganization() {
		List<Organization> organization = organizationService.findAll();
		return organization;
	}

	public void deleteFile() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> map = context.getExternalContext().getRequestParameterMap();
		String id = map.get("id");
		if (id != null) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setId(Long.parseLong(id));
			fileInfo = fileInfoService.find(fileInfo);
			try {
				File file = new File(
						appProperties.getDocinfoRootPath() + File.separatorChar + fileInfo.getUniqueFileName());
				if (!file.delete()) {
					System.err.println("File(" + file.toString() + ") not deleted");
				}
				fileInfoService.remove(fileInfo);
				String url = context.getExternalContext().getRequestContextPath() + "/admin/docinfo.xhtml";
				context.getExternalContext().redirect(url);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void fileUploadListener(FileUploadEvent event) {
		try {
			if (event.getFile() != null) {
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String ext = FilenameUtils.getExtension(event.getFile().getFileName());
				File file = File.createTempFile("IMG_" + timeStamp + "_", "." + ext,
						new File(appProperties.getDocinfoRootPath()));

				try (InputStream is = event.getFile().getInputstream()) {
					FileUtils.copyInputStreamToFile(is, file);
				}

				FileInfo fileInfo = new FileInfo();
				fileInfo.setDocInfo(getCurrent());
				fileInfo.setUniqueFileName(file.getName());
				fileInfo.setLenght(event.getFile().getSize());
				fileInfoService.persist(fileInfo);
			}
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage("Hiba feltöltés közben: " + e.getLocalizedMessage());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public void toggleEvent(ToggleEvent event) {
		Visibility visibility = event.getVisibility();
		if (visibility.equals(Visibility.VISIBLE)) {
			setCurrent((DocInfo) event.getData());
		}
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
	public boolean checkDeleteRight(DocInfo entity) throws ActionAccessDeniedException {
		entity.getFileInfos().forEach(c -> {
			File file = new File(appProperties.getDocinfoRootPath() + File.separatorChar + c.getUniqueFileName());
			if (!file.delete()) {
				System.err.println("File(" + c.getUniqueFileName() + ") not deleted");
			}
		});
		return true;
	}

	@Override
	public boolean checkEditableRights(DocInfo entity) throws ActionAccessDeniedException {
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
		return true;
	}
}
