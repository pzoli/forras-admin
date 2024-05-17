package hu.infokristaly.back.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Cacheable(value=true)
@Table(name="systemuser")
public class SystemUser implements Serializable {

    private static final long serialVersionUID = 1899784687816530303L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long userid;
    
    @Basic
    @Column(name="emailaddress")
   private String emailAddress;
    
    @Basic
    private boolean enabled;

    @Column(name="casemanager")
    @Basic
    private Boolean caseManager = false;

    @Basic
    private String userpassword;
    
    @Basic
    private String username;
    
    @Basic
    @Column(name="adminuser")
    private boolean adminUser = false;
    
    @Basic
    @Column(name="pincode")
    private String pinCode;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="deleteddate")
    private Date deletedDate;
        
    public Long getUserid() {
        return userid;
    }
    
    public void setUserid(Long userid) {
        this.userid = userid;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getUserpassword() {
        return userpassword;
    }
    
    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdminUser() {
        return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.adminUser = adminUser;
    }

    public Boolean getCaseManager() {
        return caseManager;
    }

    public void setCaseManager(Boolean caseManager) {
        this.caseManager = caseManager;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SystemUser))
            return false;
        SystemUser other = (SystemUser) obj;
        if (getUserid() == null) {
            if (other.getUserid() != null)
                return false;
        } else if (!getUserid().equals(other.getUserid()))
            return false;
        return true;
    }

}
