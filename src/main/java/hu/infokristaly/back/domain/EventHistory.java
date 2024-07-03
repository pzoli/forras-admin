package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleRenderingMode;

import hu.exprog.beecomposit.back.model.SystemUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistent class for the lesson database table.
 * 
 */
@Entity
@Table(name = "eventhistory", indexes={@Index(columnList="styleclass", name = "idx_style_class"), @Index(columnList="end_date", name = "idx_end_date"),@Index(columnList="start_date", name = "idx_start_date")})
@NamedQuery(name = "EventHistory.findAll", query = "SELECT l FROM EventHistory l")
public class EventHistory extends EventData implements ScheduleEvent, Serializable {

    private static final long serialVersionUID = 7127208526273798115L;
    
    private Long eventId;
    private String id;
    private String styleClass;

    private Long version;
    
    public EventHistory() {
    }

    public EventHistory(EventHistory currentEvent, List<Client> refreshedClients, List<SystemUser> refreshedUsers) {
        super();
        this.setAllDay(currentEvent.isAllDay());
        this.setClients(refreshedClients);
        this.setCreatedBy(currentEvent.getCreatedBy());
        this.setCreatedDate(new Date());
        this.setData(currentEvent.getData());
        this.setDescription(currentEvent.getDescription());
        this.setEditable(currentEvent.isEditable());
        this.setEndDate(currentEvent.getEndDate());
        this.setGroupForClients(currentEvent.getGroupForClients());
        this.setLeaders(refreshedUsers);
        this.setStartDate(currentEvent.getStartDate());
        this.setStyleClass(currentEvent.getStyleClass());
        this.setSubject(currentEvent.getSubject());
        this.setTitle(currentEvent.getTitle());
    }

    public EventHistory(EventTemplate currentEvent, List<Client> refreshedClients, List<SystemUser> refreshedUsers) {
        super();
        this.setAllDay(currentEvent.isAllDay());
        this.setClients(refreshedClients);
        this.setCreatedBy(currentEvent.getCreatedBy());
        this.setCreatedDate(new Date());
        this.setData(currentEvent.getData());
        this.setDescription(currentEvent.getDescription());
        this.setEditable(currentEvent.isEditable());
        this.setEndDate(currentEvent.getEndDate());
        this.setGroupForClients(currentEvent.getGroupForClients());
        this.setLeaders(refreshedUsers);
        this.setStartDate(currentEvent.getStartDate());        
        this.setSubject(currentEvent.getSubject());
        this.setTitle(currentEvent.getTitle());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    public Long getEventId() {
        return this.eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
   
    @Transient
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    
    @Override
    @Basic
    @Column(name = "styleclass")
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    @Basic
    @Override
    public boolean isEditable() {
        return super.isEditable();
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_client", joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "eventid") }, inverseJoinColumns = { @JoinColumn(name = "client_id", referencedColumnName = "id") })
    @Override
    public List<Client> getClients() {
        return super.getClients();
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_leader", joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "eventid") }, inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") })
    @Override
    public List<SystemUser> getLeaders() {
        return super.getLeaders();
    }

    @Override
    @Basic
    public boolean isAllDay() {
        return super.isAllDay();
    }

    @Version
    public Long getVersion() {
    	return version;
    }
    
    public void setVersion(Long version) {
    	this.version = version;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return "DefaultScheduleEvent{title=" + getTitle() + ",startDate=" + dateFormat.format(getStartDate()) + ",endDate=" + dateFormat.format(getEndDate()) + "}";
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((styleClass == null) ? 0 : styleClass.hashCode());
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
		EventHistory other = (EventHistory) obj;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (styleClass == null) {
			if (other.styleClass != null)
				return false;
		} else if (!styleClass.equals(other.styleClass))
			return false;
		return super.equals(obj);
	}

	@Transient
	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	@Override
	public ScheduleRenderingMode getRenderingMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	@Override
	public Map<String, Object> getDynamicProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}