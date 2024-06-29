package hu.exprog.honeyweb.utils.velocity;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class TemplateTools4Mails {

	public static String transformSource(String logFile, String workingDirectory, String templateFileName, VelocityContext context) {
		File templateDir = new File(workingDirectory);
		String absolutePath = templateDir.getAbsolutePath();

		Properties properties = new Properties();
		properties.put(RuntimeConstants.INPUT_ENCODING, StandardCharsets.UTF_8.name());
		properties.put(RuntimeConstants.OUTPUT_ENCODING, StandardCharsets.UTF_8.name());
		properties.put(RuntimeConstants.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
		properties.setProperty("runtime.log", logFile);
		properties.setProperty("file.resource.loader.path", absolutePath);
		
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init(properties);
		Template classTplFile = velocityEngine.getTemplate(templateFileName);
		StringWriter writer = new StringWriter(); 
		classTplFile.merge(context, writer);
		
		return writer.toString();
	}

}
