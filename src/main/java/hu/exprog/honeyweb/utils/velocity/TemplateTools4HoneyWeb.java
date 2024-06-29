package hu.exprog.honeyweb.utils.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.DisplayTool;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;
import hu.exprog.honeyweb.utils.ColumnModel;

public class TemplateTools4HoneyWeb {

	private final Properties properties = new Properties();
	private VelocityContext context = new VelocityContext();
	private Class<?> domainClass;
	private List<ColumnModel> columnModel;
	private Map<String, EntityFieldInfo> entityInfoMap;
	private Map<String, LookupFieldInfo> lookupFieldInfoMap;
	private Map<String,Field> fieldMap;
	private String classMainXhtmlName;
	private String tplXhtmlName;
	private String dialogXhtmlName;
	private String serviceBeanName;
	private String managerBeanName;
	private String workingDirectory;
	private String i18nName;
	private String converterName;
	private String logFile;
	private boolean createConverter;

	public TemplateTools4HoneyWeb(Class<?> domainClass, List<ColumnModel> list, Map<String, EntityFieldInfo> entityInfoMap, Map<String, LookupFieldInfo> lookupFieldInfoMap, String workingDirecotry, String logFile) {
		this.domainClass = domainClass;
		this.fieldMap = getFieldMap(domainClass);
		this.columnModel = list;
		this.entityInfoMap = entityInfoMap;
		this.lookupFieldInfoMap = lookupFieldInfoMap;
		this.workingDirectory = workingDirecotry;
		this.logFile = logFile;
	}

	private Map<String, Field> getFieldMap(Class<?> clazz) {
		Map<String, Field> result = new HashMap<String, Field>();
		for(Field field : clazz.getDeclaredFields()) {
			result.put(field.getName(), field);
		}
		return result;
	}

	public void generateXHTMLFromTemplate(String targetFolderForXhtmlMain) {
		properties.put(RuntimeConstants.INPUT_ENCODING, StandardCharsets.UTF_8.name());
		properties.put(RuntimeConstants.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());
		properties.put(RuntimeConstants.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
		File templateFilePath = new File(workingDirectory);
		String absolutePath = templateFilePath.getAbsolutePath();
		properties.setProperty("file.resource.loader.path", absolutePath);
		properties.setProperty("runtime.log", logFile);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init(properties);

		Template classTplFile = velocityEngine.getTemplate("templates/class-table-editor.tpl.xhtml.vm");
		Template classDialog = velocityEngine.getTemplate("class-dialog.xhtml.vm");
		Template classMainXhtml = velocityEngine.getTemplate("class-main.xhtml.vm");
		Template genericManager = velocityEngine.getTemplate("genericManager.java.vm");
		Template genericService = velocityEngine.getTemplate("genericService.java.vm ");
		Template generateFiles = velocityEngine.getTemplate("generated.properties.vm");
		Template i18n = velocityEngine.getTemplate("i18n.vm");
		Template converter = null;
		if (createConverter) {
			converter = velocityEngine.getTemplate("converter.vm");
		}

		context.put("display", new DisplayTool());
		context.put("String", new String());
		context.put("EntityClassName", domainClass.getSimpleName());
		context.put("fieldMap", fieldMap);
		context.put("folder", targetFolderForXhtmlMain);
		context.put("columnModel", columnModel);
		context.put("entityInfoMap", entityInfoMap);
		context.put("lookupFieldInfoMap", lookupFieldInfoMap);
		
		classMainXhtmlName = workingDirectory + "/" + domainClass.getSimpleName().toLowerCase() + ".xhtml";
		dialogXhtmlName = workingDirectory + "/" + domainClass.getSimpleName().toLowerCase() + "-dialog.xhtml";
		tplXhtmlName = workingDirectory + "/" + domainClass.getSimpleName().toLowerCase() + ".tpl.xhtml";
		managerBeanName = workingDirectory + "/" + domainClass.getSimpleName() + "Manager.java";
		serviceBeanName = workingDirectory + "/" + domainClass.getSimpleName() + "Service.java";
		i18nName = workingDirectory + "/" + domainClass.getSimpleName() + "_i18n.properties";
		converterName = workingDirectory + "/" + domainClass.getSimpleName() + "Converter.java";

		VelocityTransformUtils.transformTemplateToFile(classTplFile, context, tplXhtmlName);
		VelocityTransformUtils.transformTemplateToFile(classDialog, context, dialogXhtmlName);
		VelocityTransformUtils.transformTemplateToFile(classMainXhtml, context, classMainXhtmlName);
		VelocityTransformUtils.transformTemplateToFile(genericManager, context, managerBeanName);
		VelocityTransformUtils.transformTemplateToFile(genericService, context, serviceBeanName);
		VelocityTransformUtils.transformTemplateToFile(generateFiles, context, workingDirectory + "/" + domainClass.getSimpleName() + "-generated.properties");
		VelocityTransformUtils.transformTemplateToFile(i18n, context, i18nName);
		if (createConverter) {
			VelocityTransformUtils.transformTemplateToFile(converter, context, converterName);
		}
	}
	
	public VelocityContext getContext() {
		return context;
	}

	public void moveFile(String src, String dest) throws IOException {
		File destFile = new File(dest);
		if (destFile.exists()) {
			destFile.delete();
		}
		FileUtils.moveFile(new File(src), destFile);
	}

	public void moveFiles() {
		Properties props = new Properties();
		File files = new File(new File(workingDirectory), domainClass.getSimpleName() + "-generated.properties");
		try (FileInputStream stream = new FileInputStream(files)) {
			props.load(stream);
			moveFile(classMainXhtmlName, props.getProperty("classMainXhtmlName"));
			moveFile(tplXhtmlName, props.getProperty("tplXhtmlName"));
			moveFile(managerBeanName, props.getProperty("managerBeanName"));
			moveFile(serviceBeanName, props.getProperty("serviceBeanName"));
			moveFile(dialogXhtmlName, props.getProperty("dialogXhtmlName"));
			if (createConverter) {
				moveFile(converterName, props.getProperty("converterName"));
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Source generated."));
		} catch (IOException ex) {
			ex.printStackTrace();
			FacesMessage message = new FacesMessage("Failed: " + ex.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public boolean isCreateConverter() {
		return createConverter;
	}

	public void setCreateConverter(boolean createConverter) {
		this.createConverter = createConverter;
	}

}
