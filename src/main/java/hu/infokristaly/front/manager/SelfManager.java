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
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.back.model.UserJoinGroup;
import hu.infokristaly.back.model.UserSettings;
import hu.infokristaly.middle.service.UserService;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class SelfManager implements Serializable {

    private static final long serialVersionUID = -8140234913414827077L;

    /** The file info service. */
    @Inject
    private UserService userService;

    @Inject
    private AppProperties appProperties;

    private SystemUser currentSystemUser;

    private String language = null;

    private Properties userProperties = null;

    public SystemUser getCurrentSystemUser() {
        return currentSystemUser;
    }

    public void setCurrentSystemUser(SystemUser newSystemUser) {
        this.currentSystemUser = newSystemUser;
    }

    public void persistCurrent() {
        SystemUser user = userService.find(currentSystemUser);
        userService.persistSystemUser(currentSystemUser);

        UserJoinGroup userJoinGroup = userService.findUserJoinGroup(user.getEmailAddress());
        if (!currentSystemUser.getEmailAddress().equals(user.getEmailAddress())) {
            userService.removeUserJoinGroup(userJoinGroup);
            userJoinGroup.getUserJoinGroupId().setUserName(currentSystemUser.getEmailAddress());
            userService.persistUserJoinGroup(userJoinGroup);
        }

        Properties userProperties = new Properties();
        userProperties.put("system.lang", language);
        userService.persistUserProperties(user, userProperties);

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        session.setAttribute(UserService.LOGGED_IN_SYSTEM_USER, currentSystemUser);
        FacesMessage message = new FacesMessage("Kedves " + currentSystemUser.getUsername(), "Adatai módosultak.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void getSettings() {
        if (userService.getLoggedInSystemUser() != null) {
            List<UserSettings> userSettings = userService.getUserSettings(userService.getLoggedInSystemUser().getUserid());
            userProperties = new Properties();
            if (userSettings != null) {
                Iterator<UserSettings> iter = userSettings.iterator();
                while (iter.hasNext()) {
                    UserSettings item = iter.next();
                    userProperties.put(item.getId().getKey(), item.getValue());
                }
            }
        }
    }

    public String getLanguage() {
        if (userProperties == null) {
            getSettings();
        }
        String userLang = null;
        if (userProperties != null) {
            userLang = (String) userProperties.get("system.lang");
            if ((userService.getLoggedInSystemUser() != null) && (userLang != null)) {
                language = userLang;
            }
        }
        if (language == null) {
            language = appProperties.getDefaultLanguage();
        } else if (!language.equals(userLang) && (userLang != null)) {
            language = userLang;
        }
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
