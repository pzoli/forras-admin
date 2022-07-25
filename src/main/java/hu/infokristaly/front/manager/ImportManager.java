package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.front.controller.FileUploadController;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.UserService;
import hu.infokristaly.utils.ImportXSLX;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named
public class ImportManager implements Serializable {

    private static final long serialVersionUID = 4383679895102597398L;

    private ImportXSLX importer;

    private boolean imported = false;

    private Boolean active = true;

    @Inject
    private FileUploadController fileUploadController;

    @Inject
    private ClientsService clientsService;

    @Inject
    private UserService userService;

    @Inject
    private Logger log;

    private List<String[]> rows;

    private ClientType newClientType;

    private List<String[]> selectedRows;

    @PostConstruct
    public void init() {
        importer = new ImportXSLX();
    }

    /**
     * @return the rows
     */
    public List<String[]> getRows() {
        return rows;
    }

    /**
     * @param rows
     *            the rows to set
     */
    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public void importFile() {
        try {
            resetLists();
            importer.importXlsx(new File(fileUploadController.getClientsFileName()));
            setRows(importer.getCellList());
            imported = true;
            FacesMessage msg = new FacesMessage("Fájl feldolgozás kész", "Feltöltött sorok: " + rows.size());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            log.fine(e.getLocalizedMessage());
        }
    }

    public boolean isSelectedClient(Client client) {
        boolean result = selectedRows.stream().parallel().filter(
                p -> p[0].equals(client.getNyilvantartasiSzam())).count() > 0;
        return result;
    }

    public void createClients() {
        importer.createClients();
        List<Client> clients = importer.getClients().stream().filter(c -> isSelectedClient(c)).collect(Collectors.toList());
        Iterator<Client> iter = clients.iterator();
        try {
            int cnt = 0;
            while (iter.hasNext()) {
                Client client = null;
                try {
                    client = iter.next();
                    client.setClientType(getNewClientType());
                    client.setActive(isActive());
                    client.setCreated_by(userService.getLoggedInSystemUser());
                    client.setCreateDate(new Date());
                    clientsService.persistClient(client);
                    cnt++;
                } catch (EJBException e) {
                    FacesMessage msg = new FacesMessage("Importálási hiba", client.getNeve() + " (" + client.getNyilvantartasiSzam() + ") már rögzített kliens.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            if (cnt > 0) {
                FacesMessage msg = new FacesMessage("Importálás kész", "Feltöltött kliensek száma: " + cnt);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (EJBException e) {
            FacesMessage msg = new FacesMessage("Importálás megszakadt", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void resetLists() {
        imported = false;
        importer.resetLists();
        selectedRows = new LinkedList<String[]>();
        rows = new LinkedList<String[]>();
    }

    public List<String> getImportLog() {
        return importer.getImportResults();
    }

    /**
     * @return the imported
     */
    public boolean isImported() {
        return imported;
    }

    /**
     * @param imported
     *            the imported to set
     */
    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public ClientType getNewClientType() {
        return newClientType;
    }

    public void setNewClientType(ClientType newClientType) {
        this.newClientType = newClientType;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the selectedRows
     */
    public List<String[]> getSelectedRows() {
        return selectedRows;
    }

    /**
     * @param selectedRows
     *            the selectedRows to set
     */
    public void setSelectedRows(List<String[]> selectedRows) {
        this.selectedRows = selectedRows;
    }

}
