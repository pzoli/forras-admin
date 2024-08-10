package hu.infokristaly.back.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import hu.infokristaly.middle.service.ServerInfoService;

@Named
@ApplicationScoped
public class AppProperties implements Serializable {

    private static final long serialVersionUID = -5173014869898532933L;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    
    @Resource(mappedName = "java:jboss/forrashaz/default-page")
    private String defaultPage;

    @Resource(mappedName = "java:jboss/forrashaz/templates")
    private String templatePath;

    @Resource(mappedName = "java:jboss/forrashaz/destination")
    private String destinationPath;

    @Resource(mappedName = "java:jboss/forrashaz/docinfo-root")
    private String docinfoRootPath;

    @Resource(mappedName = "java:jboss/forrashaz/systemtimerstarthour")
    private Integer systemTimerStartHour;

    @Resource(mappedName = "java:jboss/forrashaz/default-language")
    private String defaultLanguage;

    @Resource(mappedName = "java:jboss/forrashaz/default-alertmanager-startdate")
    private String defaultAlertManagerStartDate;
    
    @Resource(mappedName = "java:jboss/forrashaz/scannerURL")
    private String scannerURL;
    

    @Inject
    private ServerInfoService serverInfoService;

    private Properties appServerProps = null;

    private Properties dbServerProps = null;    

    private String projectStage;

    public String getDefaultPage() {
        return defaultPage;
    }

    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public String getDocinfoRootPath() {
        return docinfoRootPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    /**
     * @return the systemTimerStart
     */
    public Integer getSystemTimerStartHour() {
        return systemTimerStartHour;
    }

    /**
     * @param systemTimerStartHour
     *            the systemTimerStart to set
     */
    public void setSystemTimerStart(Integer systemTimerStartHour) {
        this.systemTimerStartHour = systemTimerStartHour;
    }

    public Properties getSystemDBInfo() {
        if (dbServerProps == null) {
            dbServerProps = serverInfoService.getSystemDBInfo();
        }
        return dbServerProps;
    }

    public Properties getSystemAppServerInfo() {
        try {
            if (appServerProps == null) {
                appServerProps = serverInfoService.getSystemAppServerInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appServerProps;
    }

    public String getJSFVersion() {
        String vendor = FacesContext.class.getPackage().getImplementationTitle();
        String version = FacesContext.class.getPackage().getImplementationVersion();
        return vendor + " " + version;
    }
    
    public String getPrimefaceVersion() {
    	String result = PrimeFaces.class.getPackage().getImplementationVersion();
        return result;
    }

    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
    }
    
    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public void setProjectStage(String result) {
        this.projectStage = result;        
    }

    public String getProjectStage() {
        return this.projectStage;        
    }

    
    public Date getDefaultAlertManagerStartDate() {
        Date result = null;
        try {
            result = dateFormat.parse(defaultAlertManagerStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

	public String getScannerURL() {
		return scannerURL;
	}

	public void setScannerURL(String scannerURL) {
		this.scannerURL = scannerURL;
	}
    
}
