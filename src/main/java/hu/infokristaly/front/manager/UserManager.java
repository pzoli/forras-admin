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

import hu.infokristaly.back.model.AppProperties;
import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.model.UserJoinGroup;
import hu.exprog.beecomposit.back.model.UserJoinGroupId;
import hu.infokristaly.back.resources.IdGenerator;
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
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class UserManager implements Serializable, Converter {

    private static final long serialVersionUID = -3906612799904206781L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private UserService userService;

    @Inject
    private AppProperties appProperties;

    private Properties userProperties = null;

    private SystemUser newSystemUser;

    private String language = null;

    private Integer count = null;
    
    private boolean deletedVisible = false;

    /** The lazy data model. */
    private LazyDataModel<SystemUser> lazyDataModel;

    private SystemUser[] selectedUsers = {};

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<SystemUser> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<SystemUser>() {

                private static final long serialVersionUID = 1678907483750487431L;

                private Map<String, Object> actualfilters;

                private String actualOrderField;

                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyFileInfoDataModel] constructor finished.");
                }

                @Override
                public SystemUser getRowData(String rowKey) {
                    SystemUser systemUser = new SystemUser();
                    systemUser.setId(Long.valueOf(rowKey));
                    return userService.find(systemUser);
                }

                @Override
                public Object getRowKey(SystemUser systemUser) {
                    return systemUser.getId();
                }

                @Override
                public List<SystemUser> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count = null;
                    this.setPageSize(pageSize);
                    if (!deletedVisible) {
                        filters.put("deletedDate", "null");
                    } else {
                        filters.remove("deletedDate");
                    }
                    this.actualfilters = filters;
                    if (sortField == null) {
                        sortField = "username";
                    }
                    this.actualOrderField = sortField;
                    if (sortOrder == null) {
                        sortOrder = SortOrder.ASCENDING;
                    }
                    this.actualSortOrder = sortOrder;

                    List<SystemUser> result = (List<SystemUser>) userService.findRange(first, pageSize, actualOrderField, actualSortOrder, filters);
                    log.log(Level.INFO, "[LazyFileInfoDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = userService.count(actualfilters);
                    }
                    return count;
                }

            };
        }
        return lazyDataModel;
    }

    public void createNewSystemUser() {
        setNewSystemUser(new SystemUser());
    }

    public void onEdit(RowEditEvent event) {
        SystemUser systemUser = (SystemUser) event.getObject();
        FacesMessage msg = new FacesMessage("Kliens adatai átszerkesztve", systemUser.getUserName());
        SystemUser user = userService.find(systemUser);
        UserJoinGroup userJoinGroup = userService.findUserJoinGroup(user.getOsUserName());
        if ((systemUser.isAdminUser() != user.isAdminUser()) || !systemUser.getOsUserName().equals(user.getOsUserName())) {
            userService.removeUserJoinGroup(userJoinGroup);
            if (userJoinGroup == null) {
                userJoinGroup = new UserJoinGroup();
                userJoinGroup.setUserJoinGroupId(new UserJoinGroupId());
            }
            userJoinGroup.getUserJoinGroupId().setGroupName(systemUser.isAdminUser() ? UserService.ADMIN_GROUP : UserService.USER_GROUP);
            userJoinGroup.getUserJoinGroupId().setUserName(systemUser.getOsUserName());
            userService.persistUserJoinGroup(userJoinGroup);
        }
        userService.persistSystemUser(systemUser);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Új kliens módosítása visszavonva", ((SystemUser) event.getObject()).getUserName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public SystemUser getNewSystemUser() {
        return newSystemUser;
    }

    public void setNewSystemUser(SystemUser newSystemUser) {
        this.newSystemUser = newSystemUser;
    }

    public void persistCurrent() {
        UserJoinGroupId userJoinGroupId = new UserJoinGroupId();
        userJoinGroupId.setGroupName(newSystemUser.isAdminUser() ? UserService.ADMIN_GROUP : UserService.USER_GROUP);
        userJoinGroupId.setUserName(newSystemUser.getOsUserName());
        UserJoinGroup userJoinGroup = new UserJoinGroup();
        userJoinGroup.setUserJoinGroupId(userJoinGroupId);
        try {
            userProperties = new Properties();
            if (language == null) {
                language = appProperties.getDefaultLanguage();
            }
            userProperties.put("system.lang", language);
            userService.persistSystemUser(newSystemUser);
            userService.persistUserProperties(newSystemUser, userProperties);
        } catch (Exception ex) {
            FacesMessage msg = new FacesMessage("Sikertelen felvétel", "[" + newSystemUser.getOsUserName() + "] e-mail cím már létezik!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public SystemUser[] getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(SystemUser[] selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public boolean isDeletedVisible() {
        return deletedVisible;
    }

    public void setDeletedVisible(boolean deletedVisible) {
        this.deletedVisible = deletedVisible;
    }

    public void disableUsers() {
        for (SystemUser item : selectedUsers) {
            item.setEnabled(false);
            userService.persistSystemUser(item);
        }
    }

    public void deleteUsers() {
        for (SystemUser item : selectedUsers) {
            try {
                userService.removeSystemUser(item);
            } catch (Exception ex) {
                item.setDeletedDate(new Date());
                item.setEnabled(false);
                userService.mergeSystemUser(item);
                FacesMessage msg = new FacesMessage("Törlés", "[" + item.getUserName() + "] hivatkozás miatt nem törölhető! A felhasználót elrejtettük, így a régebbi foglalkozásokon látható marad.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        selectedUsers = new SystemUser[]{};
    }
    
    public void generateUserQRCode() {
        SystemUser user = userService.getLoggedInSystemUser();
        String oFileName = null;
        oFileName = appProperties.getDestinationPath() + new String("/user_" + StringToolkit.getCFileName(user.getUserName().replace(' ', '_')) + ".jpg");

        try {
            userService.generateQRCode(user, oFileName);
            FacesMessage msg = new FacesMessage("Sikerült", "Kód generálás sikerült.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (WriterException | IOException e) {
            FacesMessage msg = new FacesMessage("Hiba!", "Kód generálás közben hiba lépett fel. (" + e.getMessage() + ")");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateUserPINCode() {
        SystemUser loggedInUser = userService.getLoggedInSystemUser();
        loggedInUser = userService.find(loggedInUser);
        String resource = (new Date()) + loggedInUser.getOsUserName();
        try {
            String result = IdGenerator.getMD5Sum(resource.getBytes());
            loggedInUser.setPinCode(result);
            loggedInUser.setOsUserPassword(null);
            userService.persistSystemUser(loggedInUser);
            userService.getLoggedInSystemUser().setPinCode(result);
            FacesMessage msg = new FacesMessage("Sikerült", "PIN kód generálás sikerült.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (NoSuchAlgorithmException e) {
            FacesMessage msg = new FacesMessage("Hiba!", "PIN kód generálás közben hiba lépett fel. (" + e.getMessage() + ")");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            log.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        SystemUser systemUser = new SystemUser();
        systemUser.setId(Long.valueOf(value));
        systemUser = userService.find(systemUser);
        return systemUser;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return String.valueOf(((SystemUser) value).getId());
    }

}
