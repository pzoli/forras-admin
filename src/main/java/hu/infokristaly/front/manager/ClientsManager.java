/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Zoltan Papp
 * 
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Accessible;
import hu.infokristaly.back.domain.AccessibleType;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.back.domain.Doctor;
import hu.infokristaly.back.model.AppProperties;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.infokristaly.middle.service.ClientTypeService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.DoctorService;
import hu.infokristaly.middle.service.UserService;
import hu.infokristaly.utils.StringToolkit;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class ClientsManager implements Serializable, Converter {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3843708547414235388L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private ClientsService clientsService;

    @Inject
    private UserService userService;

    @Inject
    private ClientTypeService clientTypeService;

    @Inject
    private DoctorService doctorService;

    @Inject
    private AppProperties appProperties;

    private Client currentClient;

    private String[] visibleClientTypes = { "1" };

    private ClientChanges[] selectedChanges;

    private List<ClientChanges> clientChanges;

    private List<ClientType> clientTypes;

    private SystemUser currentCaseManager;

    private List<AccessibleType> accessibleTypes = new ArrayList<AccessibleType>();

    private Accessible currentAccessible = new Accessible();

    private AccessibleType newAccessibleType = new AccessibleType();

    /** The lazy data model. */
    private LazyDataModel<Client> lazyDataModel;

    private LazyDataModel<Client> lazyDataModelForDeletedUsers;

    private Client[] selectedClients = {};

    private Client[] selectedDeletedClients = {};

    private Client toggled;

    private String currentDoctorId;

    private String visibleByActive = "true";

    private Integer count = null;

    private Integer countDeleted = null;

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<Client> getLazyDataModel() {
        if (lazyDataModel == null) {
            count = null;
            lazyDataModel = new LazyDataModel<Client>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;

                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyFileInfoDataModel] constructor finished.");
                }

                @Override
                public Client getRowData(String rowKey) {
                    Client client = new Client();
                    client.setId(Long.valueOf(rowKey));
                    return clientsService.find(client);
                }

                @Override
                public Object getRowKey(Client client) {
                    if (client instanceof Client) {
                        return client.getId();
                    } else {
                        return null;
                    }
                }

                @Override
                public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count = null;
                    this.setPageSize(pageSize);

                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<Client> result = (List<Client>) clientsService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters, true, null, visibleByActive, visibleClientTypes);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = clientsService.count(actualfilters, true, null, visibleByActive, visibleClientTypes);
                    }
                    return count;
                }

            };
        }
        return lazyDataModel;
    }

    public void cleanLazyDataModel() {
        lazyDataModel = null;
    }

    public LazyDataModel<Client> getLazyDataModelForDeleted() {
        if (lazyDataModelForDeletedUsers == null) {
            countDeleted = null;
            lazyDataModelForDeletedUsers = new LazyDataModel<Client>() {

                private static final long serialVersionUID = -3594213454780970089L;

                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyDataModelForDeleted] constructor finished.");
                }

                @Override
                public Client getRowData(String rowKey) {
                    Client client = new Client();
                    client.setId(Long.valueOf(rowKey));
                    return clientsService.find(client);
                }

                @Override
                public Object getRowKey(Client client) {
                    return client.getId();
                }

                @Override
                public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    countDeleted = null;
                    this.setPageSize(pageSize);
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<Client> result = (List<Client>) clientsService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters, false, null, null, null);
                    log.log(Level.INFO, "[LazyDataModelForDeleted] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (countDeleted == null) {
                        countDeleted = clientsService.count(actualfilters, false, null, null, null);
                    }
                    return countDeleted;
                }

            };
        }
        return lazyDataModelForDeletedUsers;
    }

    @PostConstruct
    public void init() {

    }

    public void createNewClient() {
        setCurrentClient(new Client());
        prepareNewClient(currentClient);
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Kliens adatai átszerkesztve", ((Client) event.getObject()).getNeve());
        Client client = (Client) event.getObject();
        client.setModificationDate(new Date());
        client.setModified_by(userService.getLoggedInSystemUser());
        clientsService.persistClient(client);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Kliens módosítása visszavonva", ((Client) event.getObject()).getNeve());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onEditAcc(RowEditEvent event) {
        Accessible accessible = (Accessible) event.getObject();
        FacesMessage msg = new FacesMessage("Elérhetőség átszerkesztve", accessible.getAccessibleValue());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onEditHistory(RowEditEvent event) {
        ClientChanges clientChange = (ClientChanges) event.getObject();
        FacesMessage msg = new FacesMessage("Történet rekord", "átszerkesztve");
        clientChange.setModifiedAt(new Date());
        clientChange.setModifiedBy(userService.getLoggedInSystemUser());
        clientsService.mergeChange(clientChange);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancelAcc(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Elérhetőség módosítása visszavonva", ((Accessible) event.getObject()).getAccessibleValue());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void updateEditing() {

    }

    public void onCancelHistoryEdit(RowEditEvent event) {

    }

    public void onHistoryRowSelect(SelectEvent event) {

    }

    public void onRowToggle(ToggleEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Státusz: " + event.getVisibility(), "Ügyfél: " + ((Client) event.getData()).getNeve());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    public void onError() throws Exception {
        if ((currentClient != null) && ((currentClient.getNyilvantartasiSzam() == null) || (currentClient.getNeve() == null) || (currentClient.getTaj() == null))) {
            throw new Exception("Hiányzó adatok");
        }
    }

    public void undeleteSelectedItems() {
        for (Client client : selectedDeletedClients) {
            client.setDeletedDate(null);
            client.setModificationDate(new Date());
            client.setModified_by(userService.getLoggedInSystemUser());
            clientsService.persistClient(client);
        }
    }

    public void restoreSelectedItems() {
        for (Client client : selectedDeletedClients) {
            client.setMegszDatum(null);
            client.setModificationDate(new Date());
            client.setModified_by(userService.getLoggedInSystemUser());
            clientsService.persistClient(client);
        }
    }

    public void deleteSelectedItems() {
        for (Client client : selectedDeletedClients) {
            try {
                clientsService.remove(userService.getLoggedInSystemUser(), client);
            } catch (EJBTransactionRolledbackException ex) {
                FacesMessage msg = new FacesMessage("Sikertelen törlés", "[" + client.getNeve() + "] hivatkozás miatt nem törölhető!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        }
    }

    public void persistCurrent() {
        if (currentClient.getCreateDate() == null) {
            currentClient.setCreateDate(new Date());
            currentClient.setCreated_by(userService.getLoggedInSystemUser());
        } else {
            currentClient.setModificationDate(new Date());
            currentClient.setModified_by(userService.getLoggedInSystemUser());
        }
        if ((currentDoctorId != null) && !currentDoctorId.isEmpty()) {
            Doctor doctor = doctorService.find(Integer.valueOf(currentDoctorId));
            currentClient.setCurrentDoctor(doctor);
        } else {
            currentClient.setCurrentDoctor(null);
        }
        clientsService.persistClient(currentClient);
    }

    public Client[] getSelectedClients() {
        return selectedClients;
    }

    public void setSelectedClients(Client[] selectedClients) {
        this.selectedClients = selectedClients;
    }

    public void deleteClients() {
        for (Client item : selectedClients) {
            item.setDeletedDate(new Date());
            item.setActive(false);
            item.setModificationDate(new Date());
            item.setModified_by(userService.getLoggedInSystemUser());
            clientsService.persistClient(item);
        }
    }

    public void passivateClients() {
        for (Client item : selectedClients) {
            item.setMegszDatum(new Date());            
            item.setModificationDate(new Date());
            item.setModified_by(userService.getLoggedInSystemUser());
            clientsService.persistClient(item);            
        }
        selectedClients = null;
    }

    public void addNewClientChange() {
        if (currentClient.getId() != null) {
            ClientChanges cChange = new ClientChanges();
            cChange.setClient(currentClient);
            List<ClientChanges> chList = loadClientChanges(currentClient);
            if ((chList == null) || ((chList != null) && chList.size() == 0)) {
                cChange.setModifiedAt(new Date());
                cChange.setPeriodStart(currentClient.getFelvetDatum());
                cChange.setActive(currentClient.getActive());
                cChange.setClientType(currentClient.getClientType());
            } else {
                cChange.setModifiedAt(new Date());
            }
            cChange.setModifiedBy(userService.getLoggedInSystemUser());
            clientsService.persistClientChanges(cChange);
            chList = loadClientChanges(currentClient);
            setClientChanges(chList);
        } else {
            FacesMessage msg = new FacesMessage("Kérem először mentse el az új kliens adatokat!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void changeClientsManager() {
        for (Client item : selectedClients) {
            item.setCurrentManager(currentCaseManager);
            item.setModificationDate(new Date());
            item.setModified_by(userService.getLoggedInSystemUser());
            clientsService.persistClient(item);
        }
    }

    public SystemUser getCurrentCaseManager() {
        return currentCaseManager;
    }

    public void setCurrentCaseManager(SystemUser currentCaseManager) {
        this.currentCaseManager = currentCaseManager;
    }

    public void prepareNewClient(Client client) {
        List<AccessibleType> accTypes = clientsService.findAllAccessibleTypes();
        currentCaseManager = null;
        setAccessibleTypes(accTypes);
        currentAccessible = new Accessible();
        if (client.getId() != null) {
            currentClient = clientsService.find(client);
            currentCaseManager = currentClient.getCurrentManager();
        } else {
            currentClient.setClientType(new ClientType(1));
            currentClient.setActive(true);
            currentCaseManager = userService.getLoggedInSystemUser();
            currentClient.setCurrentManager(currentCaseManager);
            currentDoctorId = "";
            setClientChanges(null);
        }
    }

    public void prepareCurrentClient(Client client) {
        List<AccessibleType> accTypes = clientsService.findAllAccessibleTypes();
        setAccessibleTypes(accTypes);
        currentAccessible = new Accessible();
        if ((client.getCurrentDoctor() != null)) {
            currentDoctorId = client.getCurrentDoctor().getId().toString();
        } else {
            currentDoctorId = "";
        }
        if (client.getId() != null) {
            currentClient = clientsService.find(client);
            currentCaseManager = currentClient.getCurrentManager();
            setClientChanges(loadClientChanges(currentClient));
        } else {
            currentCaseManager = null;
            setClientChanges(null);
        }
    }

    public void createNewAccessibleForCurrentClient() {
        currentAccessible = new Accessible();
    }

    public void persistAccessible(Accessible acc) {

    }

    public void persistCurrentAccessible() {
        int idx = currentClient.getAccessibles().indexOf(currentAccessible);
        if (idx > -1) {
            currentClient.getAccessibles().remove(idx);
            currentClient.getAccessibles().add(currentAccessible);
        }
    }

    public void persistNewAccessible() {
        if ((currentAccessible != null) && (currentAccessible.getAccessibleValue() != null) && (!currentAccessible.getAccessibleValue().isEmpty())) {
            currentAccessible.setAccessible_type(newAccessibleType);
            currentClient.getAccessibles().add(currentAccessible);
            currentAccessible = new Accessible();
        }
    }

    public List<AccessibleType> getAccessibleTypes() {
        return accessibleTypes;
    }

    public void setAccessibleTypes(List<AccessibleType> accessibleTypes) {
        this.accessibleTypes = accessibleTypes;
    }

    public Accessible getCurrentAccessible() {
        return currentAccessible;
    }

    public void setCurrentAccessible(Accessible currentAccessible) {
        this.currentAccessible = currentAccessible;
    }

    public Client getToggled() {
        return toggled;
    }

    public void setToggled(Client toggled) {
        this.toggled = toggled;
    }

    /**
     * @return the selectedDeletedClients
     */
    public Client[] getSelectedDeletedClients() {
        return selectedDeletedClients;
    }

    /**
     * @param selectedDeletedClients
     *            the selectedDeletedClients to set
     */
    public void setSelectedDeletedClients(Client[] selectedDeletedClients) {
        this.selectedDeletedClients = selectedDeletedClients;
    }

    /**
     * @return the newAccessibleType
     */
    public AccessibleType getNewAccessibleType() {
        return newAccessibleType;
    }

    /**
     * @param newAccessibleType
     *            the newAccessibleType to set
     */
    public void setNewAccessibleType(AccessibleType newAccessibleType) {
        this.newAccessibleType = newAccessibleType;
    }

    /**
     * @return the visibleClientTypes
     */
    public String[] getVisibleClientTypes() {
        return visibleClientTypes;
    }

    /**
     * @param visibleClientTypes
     *            the visibleClientTypes to set
     */
    public void setVisibleClientTypes(String[] visibleClientTypes) {
        this.visibleClientTypes = visibleClientTypes;
    }

    public void generateClientQRCode() {
        String oFileName = appProperties.getDestinationPath() + "/client_" + StringToolkit.getCFileName(currentClient.getNeve().replace(' ', '_')) + "_" + currentClient.getNyilvantartasiSzam().trim().replace('/', '-') + ".jpg";
        try {
            clientsService.generateQRCode(currentClient, oFileName);
            FacesMessage msg = new FacesMessage("Sikerült.", "Kód generálás sikerült");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (WriterException | IOException e) {
            FacesMessage msg = new FacesMessage("Hiba!", "Kód generálás közben hiba lépett fel. (" + e.getMessage() + ")");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateClientQRCodeForSelecteds() {
        for (Client item : selectedClients) {
            String oFileName = appProperties.getDestinationPath() + "/client_" + StringToolkit.getCFileName(item.getNeve().replace(' ', '_')) + "_" + item.getNyilvantartasiSzam().trim().replace('/', '-') + ".jpg";
            try {
                clientsService.generateQRCode(item, oFileName);
            } catch (WriterException | IOException e) {
                FacesMessage msg = new FacesMessage("Hiba! (" + item.getNyilvantartasiSzam() + ")", "Kód generálás közben hiba lépett fel. (" + e.getMessage() + ")");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                log.severe(e.getMessage());
                e.printStackTrace();
            }
        }
        FacesMessage msg = new FacesMessage("Befejeződött.", "A tömeges kód generálás befejeződött.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<ClientType> getClientTypes() {
        return clientTypes;
    }

    public void setClientTypes(List<ClientType> clientTypes) {
        this.clientTypes = clientTypes;
    }

    /**
     * @return the clientTypeService
     */
    public ClientTypeService getClientTypeService() {
        return clientTypeService;
    }

    /**
     * @param clientTypeService
     *            the clientTypeService to set
     */
    public void setClientTypeService(ClientTypeService clientTypeService) {
        this.clientTypeService = clientTypeService;
    }

    /**
     * @return the visibleByActive
     */
    public String getVisibleByActive() {
        return visibleByActive;
    }

    /**
     * @param visibleByActive
     *            the visibleByActive to set
     */
    public void setVisibleByActive(String visibleByActive) {
        this.visibleByActive = visibleByActive;
    }

    /**
     * @return the selectedChanges
     */
    public ClientChanges[] getSelectedChanges() {
        return selectedChanges;
    }

    /**
     * @param selectedChanges
     *            the selectedChanges to set
     */
    public void setSelectedChanges(ClientChanges[] selectedChanges) {
        this.selectedChanges = selectedChanges;
    }

    /**
     * @return the clientChanges
     */
    public List<ClientChanges> getClientChanges() {
        return clientChanges;
    }

    /**
     * @param clientChanges
     *            the clientChanges to set
     */
    public void setClientChanges(List<ClientChanges> clientChanges) {
        this.clientChanges = clientChanges;
    }

    /**
     * @return the currentDoctorId
     */
    public String getCurrentDoctorId() {
        return currentDoctorId;
    }

    /**
     * @param currentDoctorId
     *            the currentDoctorId to set
     */
    public void setCurrentDoctorId(String currentDoctorId) {
        this.currentDoctorId = currentDoctorId;
    }

    public void updateClientList() {
        log.info("Update filtered client list");
        for (String type : visibleClientTypes) {
            log.info(type);
        }
    }

    public List<ClientChanges> loadClientChanges(Client client) {
        return clientsService.findClientAllChange(client,false);
    }

    public void removeSelectedHistory() {
        if ((selectedChanges != null) && (selectedChanges.length > 0)) {
            clientsService.removeClientChanges(selectedChanges);
            setClientChanges(loadClientChanges(currentClient));
        }
    }

    public void deleteAccessibles(Accessible acc) {
        int idx = currentClient.getAccessibles().indexOf(acc);
        if (idx > -1) {
            currentClient.getAccessibles().remove(idx);
        }
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Client client = new Client();
        client.setId(Long.valueOf(value));
        client = clientsService.find(client);
        return client;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((Client) value).getId());
    }

}
