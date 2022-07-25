package hu.infokristaly.back.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class UserSettings implements Serializable {

    public UserSettings() {
        
    }
            
    public UserSettings(Long userid, Object k, Object v) {
        id = new UserSettingsId(userid, k);
        this.value = (String)v;
    }

    private static final long serialVersionUID = -2872224372261912199L;

    @EmbeddedId
    private UserSettingsId id;
    
    @Basic
    private String value;

    public UserSettingsId getId() {
        return id;
    }
    
    public void setId(UserSettingsId id) {
        this.id = id;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    
}
