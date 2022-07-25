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

import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.middle.service.ClientTypeService;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class ClientTypeManager implements Serializable {

    private static final long serialVersionUID = -3562322678573374746L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private ClientTypeService clientTypeService;

    private ClientType newClientType = new ClientType();

    private ClientType currentClientType = new ClientType();

    /** The lazy data model. */
    private LazyDataModel<ClientType> lazyDataModel;

    private Integer count = null;

    private ClientType[] selectedClientType = {};

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<ClientType> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<ClientType>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyClientTypeDataModel] constructor finished.");
                }

                @Override
                public ClientType getRowData(String rowKey) {
                    Integer primaryKey = Integer.valueOf(rowKey);
                    return clientTypeService.findClientType(primaryKey);
                }

                @Override
                public Object getRowKey(ClientType clientType) {
                    return clientType.getId();
                }

                @Override
                public List<ClientType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count = null;
                    this.setPageSize(pageSize);
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<ClientType> result = (List<ClientType>) clientTypeService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = clientTypeService.count(actualfilters);
                    }
                    return count;
                }

            };
        }
        return lazyDataModel;
    }

    public void createCurrentClientType() {
        setCurrentClientType(new ClientType());
    }

    public void createNewSubject() {
        setNewClientType(new ClientType());
    }

    public void onEdit(RowEditEvent event) {
        ClientType clientType = (ClientType) event.getObject();
        FacesMessage msg = new FacesMessage("Ellátás jellege átszerkesztve", clientType.getTypename());
        clientTypeService.mergeClientType(clientType);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Módosítása visszavonva", ((ClientType) event.getObject()).getTypename());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public ClientType getCurrentClientType() {
        return currentClientType;
    }

    public void setCurrentClientType(ClientType clientType) {
        this.currentClientType = clientType;
    }

    public void persistNew() {
        clientTypeService.persistClientType(newClientType);
        createNewSubject();
    }

    public void persistCurrent() {
        clientTypeService.persistClientType(currentClientType);
        createCurrentClientType();
    }

    public ClientType[] getSelectedClientType() {
        return selectedClientType;
    }

    public void setSelectedClientType(ClientType[] selectedClientType) {
        this.selectedClientType = selectedClientType;
    }

    public void deleteClientType() {
        for (ClientType item : selectedClientType) {
            try {
                clientTypeService.deleteClientType(item);
            } catch (EJBTransactionRolledbackException ex) {
                FacesMessage msg = new FacesMessage("Sikertelen törlés", "[" + item.getTypename() + "] hivatkozás miatt nem törölhető!");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public ClientType getNewClientType() {
        return newClientType;
    }

    public void setNewClientType(ClientType newClientType) {
        this.newClientType = newClientType;
    }

}
