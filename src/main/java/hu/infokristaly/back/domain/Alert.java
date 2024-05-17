package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

import hu.infokristaly.back.model.SystemUser;

import java.util.Date;


@Entity
@Table(name="alerts")
@NamedQuery(name="Alert.findAll", query="SELECT a FROM Alert a")
public class Alert implements Serializable {

    private static final long serialVersionUID = -4776004061222253312L;

    private Integer id;
    private String title;
    private Long nDay;
	private Client client;
	private ClientType clientType;
	private Boolean active;
	private Date lastVisit;
	private SystemUser createdBy;

	public Alert() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }


    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="client")
	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@Temporal(TemporalType.TIMESTAMP)
    public Date getLastVisit() {
        return lastVisit;
    }


    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="createdby")
    public SystemUser getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(SystemUser createdBy) {
        this.createdBy = createdBy;
    }


    /**
     * @return the nDay
     */
    @Basic
    public Long getNDay() {
        return nDay;
    }


    /**
     * @param nDay the nDay to set
     */
    public void setNDay(Long nDay) {
        this.nDay = nDay;
    }


    /**
     * @return the version
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="clientType")
    public ClientType getClientType() {
        return clientType;
    }


    /**
     * @param clientType the type of client
     */
    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }


    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }


    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastVisit == null) ? 0 : lastVisit.hashCode());
		result = prime * result + ((nDay == null) ? 0 : nDay.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alert other = (Alert) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (clientType == null) {
			if (other.clientType != null)
				return false;
		} else if (!clientType.equals(other.clientType))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastVisit == null) {
			if (other.lastVisit != null)
				return false;
		} else if (!lastVisit.equals(other.lastVisit))
			return false;
		if (nDay == null) {
			if (other.nDay != null)
				return false;
		} else if (!nDay.equals(other.nDay))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}