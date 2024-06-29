/**
 * 
 */
package hu.exprog.beecomposit.back.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserJoinGroupId implements Serializable {

    private static final long serialVersionUID = -5903187944642055379L;

    @Basic
    @Column(name="user_name")
    private String userName;
    
    @Basic
    @Column(name="group_name")
    private String groupName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof UserJoinGroupId))
            return false;
        UserJoinGroupId castOther = (UserJoinGroupId) other;

        return ((this.getUserName() == castOther.getUserName()) || (this.getUserName() != null && castOther.getUserName() != null && this.getUserName().equals(castOther.getUserName())))
                && ((this.getGroupName() == castOther.getGroupName()) || (this.getGroupName() != null && castOther.getGroupName() != null && this.getGroupName().equals(castOther.getGroupName())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + (getUserName() == null ? 0 : this.getUserName().hashCode());
        result = 37 * result + (getGroupName() == null ? 0 : this.getGroupName().hashCode());
        return result;
    }

}
