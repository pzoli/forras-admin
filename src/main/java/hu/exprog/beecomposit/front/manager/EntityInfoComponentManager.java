package hu.exprog.beecomposit.front.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.extensions.model.dynaform.DynaFormModel;

import hu.exprog.beecomposit.back.model.EntityInfoComponent;
import hu.exprog.beecomposit.middle.service.EntityInfoComponentService;
import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;
import hu.exprog.honeyweb.front.exceptions.ActionAccessDeniedException;
import hu.exprog.honeyweb.front.manager.BasicManager;
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.exprog.honeyweb.utils.ColumnModel;
import hu.exprog.honeyweb.utils.FieldModel;
import hu.exprog.honeyweb.utils.LookupFieldModel;
import hu.exprog.honeyweb.utils.velocity.TemplateTools4HoneyWeb;

@Named
@WindowScoped
public class EntityInfoComponentManager extends BasicManager<EntityInfoComponent> implements Serializable {

	private static final long serialVersionUID = 1752031081931248632L;

	@Inject
	private Logger logger;

	private String packageName = "hu.seacon.back.model";
	private String clazzName = "GepjarmuMarka";
	private String xhtmlTargetDir = "admin";
	private String projectHomeDir = "d:/development/exprog/workspace/Homework4Telekocsi";
	private String packageSubDir = "seacon";
	private String templatesDir = "d:/work/exprog";
	private String logFile = "d:/work/velocity.log";
	private String functionLabel = "Gépjármű márka";
	private Boolean moveFilesToTarget = false;

	@Inject
	private LocaleManager localeManager;

	private EntityInfoComponentService entityInfoComponentService;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected BasicService<EntityInfoComponent> getService() {
		return entityInfoComponentService;
	}

	@Override
	protected Object getDetailFieldValue(LookupFieldModel model) {
		return null;
	}

	@Override
	protected Locale getLocale() {
		return localeManager.getLocale();
	}

	@Override
	public boolean checkListRight() throws ActionAccessDeniedException {
		return false;
	}

	@Override
	public boolean checkSaveRight() throws ActionAccessDeniedException {
		return false;
	}

	@Override
	public boolean checkDeleteRight() throws ActionAccessDeniedException {
		return false;
	}

	@Override
	public Object postProcess(FieldModel field, Object value) {
		return null;
	}

	@Override
	public Object preProcess(FieldModel field, Object value) {
		return null;
	}

	public String getPackageSubDir() {
		return packageSubDir;
	}

	public void setPackageSubDir(String packageSubDir) {
		this.packageSubDir = packageSubDir;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public String getXhtmlTargetDir() {
		return xhtmlTargetDir;
	}

	public void setXhtmlTargetDir(String xhtmlTargetDir) {
		this.xhtmlTargetDir = xhtmlTargetDir;
	}

	public String getProjectHomeDir() {
		return projectHomeDir;
	}

	public void setProjectHomeDir(String projectHomeDir) {
		this.projectHomeDir = projectHomeDir;
	}

	public String getTemplatesDir() {
		return templatesDir;
	}

	public void setTemplatesDir(String templatesDir) {
		this.templatesDir = templatesDir;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getFunctionLabel() {
		return functionLabel;
	}

	public void setFunctionLabel(String functionLabel) {
		this.functionLabel = functionLabel;
	}

	public Boolean getMoveFilesToTarget() {
		return moveFilesToTarget;
	}

	public void setMoveFilesToTarget(Boolean moveFilesToTarget) {
		this.moveFilesToTarget = moveFilesToTarget;
	}

	public void generateComponentsFromTemplate() {
		Class<?> clazz;
		try {
			clazz = Class.forName(packageName + "." + clazzName);
			List<ColumnModel> initColumns = new ArrayList<ColumnModel>();
			DynaFormModel initFormModel = new DynaFormModel();
			Map<String, EntityFieldInfo> entityInfoMap = new HashMap<String, EntityFieldInfo>();
			Locale initLocale = getLocale();
			Map<String, LookupFieldInfo> lookupFieldInfoMap = getLookupFieldInfoMap(clazz);
			prepareModel(clazz, initColumns, initFormModel, initLocale, null);
			TemplateTools4HoneyWeb templateTools = new TemplateTools4HoneyWeb(clazz, initColumns, entityInfoMap, lookupFieldInfoMap, templatesDir, logFile);
			templateTools.getContext().put("genericProjectFolder", projectHomeDir);
			templateTools.getContext().put("packageSubDir", packageSubDir);
			templateTools.getContext().put("functionLabel", functionLabel);
			templateTools.generateXHTMLFromTemplate(xhtmlTargetDir);
			if (moveFilesToTarget) {
				templateTools.moveFiles();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkEditableRights(EntityInfoComponent entity) throws ActionAccessDeniedException {
		return true;
	}

	@Override
	public boolean checkDeleteRight(EntityInfoComponent entity) throws ActionAccessDeniedException {
		return true;
	}

}
