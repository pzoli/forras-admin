package hu.infokristaly.back.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

@Embeddable
public class UserSettingsId implements Serializable {
    
    private static final long serialVersionUID = -4171618352959493371L;

    @Basic
    private String key;
    
    @Basic
    private Long userId;

    public UserSettingsId() {
        
    }
    
    public UserSettingsId(Long userid, Object k) {
        this.userId=userid;
        this.key = (String)k;
    }
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserSettingsId))
            return false;
        UserSettingsId other = (UserSettingsId) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }
    
}
