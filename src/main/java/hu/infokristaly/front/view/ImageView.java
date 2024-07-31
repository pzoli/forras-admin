package hu.infokristaly.front.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import hu.infokristaly.back.model.AppProperties;

import java.io.Serializable;
import java.util.Map;

@Named
@RequestScoped
public class ImageView implements Serializable {

	private static final long serialVersionUID = -1067751029307336424L;
	private DefaultStreamedContent image;

	@Inject
	private AppProperties appProperties;

	public ImageView() {
	}

	@PostConstruct
	public void setStream() {
		image = new DefaultStreamedContent();
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> map = context.getExternalContext().getRequestParameterMap();
		String fileName = map.get("fileName");
		if (fileName != null && !fileName.isEmpty()) {
			image.setContentType("image/jpeg");
			FileInputStream fis;
			try {
				fis = new FileInputStream(new File(appProperties.getDocinfoRootPath() + File.separatorChar + fileName));
				image.setStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public StreamedContent getImage() {
		return image;
	}
}