package hu.infokristaly.back.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import hu.exprog.beecomposit.back.model.SystemUser;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="loggedByUser")    
    private SystemUser loggedByUser;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTimestamp; 
    
    @Type(type = "org.hibernate.type.TextType")
    @Lob
    @Column(columnDefinition="text")
    private String loggedValue;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="loggedFunction")    
    private LoggedFunction loggedFunction;
    
    @Basic
    private byte action;
    
    public Long getId() {
        return id;
    }

    public SystemUser getLoggedByUser() {
        return loggedByUser;
    }

    public Date getLogTimestamp() {
        return logTimestamp;
    }

    public String getLoggedValue() {
        return loggedValue;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLoggedByUser(SystemUser loggedByUser) {
        this.loggedByUser = loggedByUser;
    }

    public void setLogTimestamp(Date logTimestamp) {
        this.logTimestamp = logTimestamp;
    }

    public void setLoggedValue(String loggedValue) {
        this.loggedValue = loggedValue;
    }

    public LoggedFunction getLoggedFunction() {
        return loggedFunction;
    }

    public void setLoggedFunction(LoggedFunction loggedFunction) {
        this.loggedFunction = loggedFunction;
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
    }
    
}
