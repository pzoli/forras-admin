package hu.infokristaly.front.controller;

import hu.infokristaly.back.model.AppProperties;
import hu.infokristaly.front.manager.ImportManager;
import hu.infokristaly.utils.StreamUtils4Me;

import java.io.FileOutputStream;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.UploadedFile;

import java.io.Serializable;

@SessionScoped
@Named
public class FileUploadController implements Serializable {
    private static final long serialVersionUID = -5551047609372436861L;

    final static int DEFAULT_BUFFER_SIZE = 4096;
    
    private UploadedFile clientsXslx;
    private String clientsFileName;
    
    @Inject 
    private ImportManager importManager;

    @Inject
    private AppProperties appProperties;

    public UploadedFile getClientsXslx() {
        return clientsXslx;
    }


    public void setClientsXslx(UploadedFile clientsXslx) {
        this.clientsXslx = clientsXslx;
    }


    public void upload() {
        try {
            clientsFileName = appProperties.getDestinationPath() + "/" + clientsXslx.getFileName();
            
            FileOutputStream fout = new FileOutputStream(clientsFileName);
            StreamUtils4Me.copy(clientsXslx.getInputstream(), fout, 1024);
            
            clientsXslx.getInputstream().close();
            importManager.importFile();
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage("Hiba feltöltés közben: "+e.getLocalizedMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }


    /**
     * @return the clientsFile
     */
    public String getClientsFileName() {
        return clientsFileName;
    }


    /**
     * @param clientsFileName the clientsFile to set
     */
    public void setClientsFileName(String clientsFileName) {
        this.clientsFileName = clientsFileName;
    }
}
