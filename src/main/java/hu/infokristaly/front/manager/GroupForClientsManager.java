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

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.GroupForClientsService;
import hu.infokristaly.middle.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class GroupForClientsManager implements Serializable {

    private static final long serialVersionUID = -3013513467182561527L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private GroupForClientsService groupForClientsService;

    @Inject
    private ClientsService clientsService;

    @Inject
    private UserService userService;

    private GroupForClients newGroup;

    private GroupForClients[] selectedGroups = {};

    private DualListModel<Client> clientsModel;

    /** The lazy data model. */
    private LazyDataModel<GroupForClients> lazyDataModel;
    
    private Integer groupCount = null;

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<GroupForClients> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<GroupForClients>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyFileInfoDataModel] constructor finished.");
                }

                @Override
                public GroupForClients getRowData(String rowKey) {
                    GroupForClients group = new GroupForClients();
                    group.setId(Integer.valueOf(rowKey));
                    return groupForClientsService.find(group);
                }

                @Override
                public Object getRowKey(GroupForClients group) {
                    return group.getId();
                }

                @Override
                public List<GroupForClients> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    groupCount = null;
                    this.setPageSize(pageSize);
                    this.actualfilters = filters;
                    List<GroupForClients> result = (List<GroupForClients>) groupForClientsService.findRange(first, pageSize, sortField, sortOrder, filters);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (groupCount == null) {
                        groupCount = groupForClientsService.count(actualfilters);
                    }
                    return groupCount;
                }

            };
        }
        return lazyDataModel;
    }

    @PostConstruct
    public void init() {

    }

    public void createNewGroup() {
        setNewGroup(new GroupForClients());
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Csoport adatai átszerkesztve", ((GroupForClients) event.getObject()).getName());
        groupForClientsService.persistGroup((GroupForClients) event.getObject());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Csoport módosítása visszavonva", ((GroupForClients) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void save(GroupForClients group) {
        clientsModel.getSource().clear();
        group.setCreatedBy(userService.getLoggedInSystemUser());
        groupForClientsService.persistGroup(group);
    }

    @SuppressWarnings("unchecked")
    public void onTransferCId(TransferEvent event) {
        List<String> clients = (List<String>) event.getItems();
        Iterator<String> iter = clients.iterator();
        if (event.isAdd()) {
            while (iter.hasNext()) {
                String clientId = iter.next();
                Client clientItem = new Client();
                clientItem.setId(Long.valueOf(clientId));
                clientItem = clientsService.find(clientItem);
                newGroup.getClients().add(clientItem);
            }
        } else {
            while (iter.hasNext()) {
                String clientId = iter.next();
                Client clientItem = new Client();
                clientItem.setId(Long.valueOf(clientId));
                clientItem = clientsService.find(clientItem);
                newGroup.getClients().remove(clientItem);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void onTransfer(TransferEvent event) {
        List<Client> clients = (List<Client>) event.getItems();
        Iterator<Client> iter = clients.iterator();
        if (event.isAdd()) {
            while (iter.hasNext()) {
                Client clientItem = (Client)iter.next();
                newGroup.getClients().add(clientItem);
            }
        } else {
            while (iter.hasNext()) {
                Client clientItem = (Client)iter.next();
                newGroup.getClients().remove(clientItem);
            }
        }
    }

    public GroupForClients getNewGroup() {
        return newGroup;
    }

    public void setNewGroup(GroupForClients newGroup) {
        this.newGroup = newGroup;
        ArrayList<Client> clientsList = new ArrayList<Client>(clientsService.findAll(true, null, null, null));
        if ((newGroup.getClients() == null) || (newGroup.getClients().size() == 0)) {
            newGroup.setClients(new ArrayList<Client>());
        } else {
            for (Client client : newGroup.getClients()) {
                if (clientsList.contains(client)) {
                    clientsList.remove(client);
                }
            }
        }
        clientsModel = new DualListModel<Client>(clientsList, newGroup.getClients());
    }

    public void persistCurrent() {
        groupForClientsService.persistGroup(newGroup);
    }

    public GroupForClients[] getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(GroupForClients[] selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public DualListModel<Client> getClientsModel() {
        if (clientsModel == null) {
            ArrayList<Client> sourceList = new ArrayList<Client>();
            ArrayList<Client> targetList = new ArrayList<Client>();
            clientsModel = new DualListModel<>(sourceList, targetList);
        }
        return clientsModel;
    }

    public void setClientsModel(DualListModel<Client> clientsModel) {
        this.clientsModel = clientsModel;
    }

    public void deleteSelected() {
        groupForClientsService.delete(selectedGroups);
    }

}
