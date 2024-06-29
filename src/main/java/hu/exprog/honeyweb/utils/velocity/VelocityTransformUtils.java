package hu.exprog.honeyweb.utils.velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class VelocityTransformUtils {

	public static void transformTemplateToFile(Template template, VelocityContext context, String file) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(file));
			template.merge(context, writer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
