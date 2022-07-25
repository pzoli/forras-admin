package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import hu.infokristaly.back.model.SystemUser;

@Entity
@Table(name="event_template")
@NamedQuery(name="EventTemplate.findAll", query="SELECT a FROM EventTemplate a")
public class EventTemplate extends EventData implements Serializable {

    private static final long serialVersionUID = 6035757883278900570L;

    private Long id;    
    private String commentOnEvent;    
    private Date periodStartDate;        
    private Byte period;
    private Boolean enabled;
    private Boolean enabledOnWeekend;
    private String lastEmailAddress;
    
    public EventTemplate() {
    }
    
    public EventTemplate(EventHistory event) {
        super();
        setTitle(event.getTitle());
        setLastEmailAddress(event.getCreatedBy().getEmailAddress());
        setSubject(event.getSubject());
        setAllDay(event.isAllDay());
        setClients(event.getClients());
        setCreatedBy(event.getCreatedBy());
        setCreatedDate(event.getCreatedDate());
        setData(event.getData());
        setDescription(event.getDescription());
        setGroupForClients(event.getGroupForClients());
        setStartDate(event.getStartDate());
        setEndDate(event.getEndDate());
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    public String getCommentOnEvent() {
        return commentOnEvent;
    }

    public void setCommentOnEvent(String comment) {
        this.commentOnEvent = comment;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Basic
    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date startDate) {
        this.periodStartDate = startDate;
    }

    @Basic
    public Byte getPeriod() {
        return period;
    }

    public void setPeriod(Byte period) {
        this.period = period;
    }

    @Basic
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Basic
    public String getLastEmailAddress() {
        return lastEmailAddress;
    }

    public void setLastEmailAddress(String lastEmailAddress) {
        this.lastEmailAddress = lastEmailAddress;
    }
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_client", joinColumns = { @JoinColumn(name = "template_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "client_id", referencedColumnName = "id") })
    @Override
    public List<Client> getClients() {
        return super.getClients();
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "template_leader", joinColumns = { @JoinColumn(name = "template_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "userid") })
    @Override
    public List<SystemUser> getLeaders() {
        return super.getLeaders();
    }

    @Override
    @Lob
    public Object getData() {
        return super.getData();
    }

    @Override
    @Basic
    public String getTitle() {
        return super.getTitle();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    public SystemUser getCreatedBy() {
        return super.getCreatedBy();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @Override
    public Date getCreatedDate() {
        return super.getCreatedDate();
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_date")
    @Override
    public Date getModificationDate() {
        return super.getModificationDate();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    @Override
    public SystemUser getModifiedBy() {
        return super.getModifiedBy();
    }

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @NotNull
    @Override
    public Subject getSubject() {
        return super.getSubject();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @Override
    public GroupForClients getGroupForClients() {
        return super.getGroupForClients();
    }

    @Basic
    @Override
    public String getDescription() {
        return super.getDescription();
    }
   
    @Temporal(TemporalType.TIMESTAMP)    
    @Column(name = "start_date")
    @Override
    public Date getStartDate() {
        return super.getStartDate();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    @Override
    public Date getEndDate() {
        return super.getEndDate();
    }

    @Override
    @Basic
    public boolean isAllDay() {
        return super.isAllDay();
    }

    @Basic
    public Boolean getEnabledOnWeekend() {
        return enabledOnWeekend;
    }

    public void setEnabledOnWeekend(Boolean enabledOnWeekend) {
        this.enabledOnWeekend = enabledOnWeekend;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((commentOnEvent == null) ? 0 : commentOnEvent.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((enabledOnWeekend == null) ? 0 : enabledOnWeekend.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastEmailAddress == null) ? 0 : lastEmailAddress.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((periodStartDate == null) ? 0 : periodStartDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventTemplate other = (EventTemplate) obj;
		if (commentOnEvent == null) {
			if (other.commentOnEvent != null)
				return false;
		} else if (!commentOnEvent.equals(other.commentOnEvent))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (enabledOnWeekend == null) {
			if (other.enabledOnWeekend != null)
				return false;
		} else if (!enabledOnWeekend.equals(other.enabledOnWeekend))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastEmailAddress == null) {
			if (other.lastEmailAddress != null)
				return false;
		} else if (!lastEmailAddress.equals(other.lastEmailAddress))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (periodStartDate == null) {
			if (other.periodStartDate != null)
				return false;
		} else if (!periodStartDate.equals(other.periodStartDate))
			return false;
		return super.equals(obj);
	}
}
