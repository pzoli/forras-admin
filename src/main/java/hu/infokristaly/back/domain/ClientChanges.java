package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

import hu.infokristaly.back.model.SystemUser;

import java.util.Date;


@Entity
@Table(name="clientchanges")
@NamedQuery(name="ClientChanges.findAll", query="SELECT a FROM ClientChanges a")
public class ClientChanges implements Serializable {

    private static final long serialVersionUID = -4776004061222253312L;

    private Integer id;
    private ClientType clientType;
    private Boolean active;
	private Client client;
	private Date periodStart;
	private Date modifiedAt;
	private SystemUser modifiedBy;
	private Byte changeType = new Byte((byte)0);
	
	public ClientChanges() {
	}


	public ClientChanges(ClientChanges item) {
        this.client = item.client;
        this.active = item.active;
        this.modifiedAt = item.modifiedAt;
        this.modifiedBy = item.modifiedBy;
        this.clientType = item.clientType;
        this.periodStart = item.periodStart;
        this.changeType = item.changeType;
    }


    public ClientChanges(Client client) {
        this.client = client;
        this.active = client.getActive();
        this.modifiedAt = client.getCreateDate();
        this.modifiedBy = client.getCreated_by();
        this.clientType = client.getClientType();
        this.periodStart = client.getFelvetDatum();
        this.clientType = client.getClientType();
        this.changeType = (byte)0;
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
    @ManyToOne
    @JoinColumn(name="clientType")
    public ClientType getClientType() {
        return clientType;
    }


    /**
     * @param title the title to set
     */
    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }


    @ManyToOne(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name="client")
	public Client getClient() {
		return this.client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	@Temporal(TemporalType.TIMESTAMP)
    public Date getModifiedAt() {
        return modifiedAt;
    }


    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }


    /**
     * @return the periodStart
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPeriodStart() {
        return periodStart;
    }


    /**
     * @param periodStart the periodStart to set
     */
    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="modifiedby")
    public SystemUser getModifiedBy() {
        return modifiedBy;
    }


    public void setModifiedBy(SystemUser modifiedBy) {
        this.modifiedBy = modifiedBy;
    }


    /**
     * @return the Active
     */
    @Basic
    public Boolean getActive() {
        return active;
    }


    /**
     * @param active the Active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }


    /**
     * @return the changeTyepe
     */
    @Basic
    public Byte getChangeType() {
        return changeType;
    }


    /**
     * @param changeTyepe the changeTyepe to set
     */
    public void setChangeType(Byte changeType) {
        this.changeType = changeType;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((changeType == null) ? 0 : changeType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedAt == null) ? 0 : modifiedAt.hashCode());
		result = prime * result + ((periodStart == null) ? 0 : periodStart.hashCode());
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
		ClientChanges other = (ClientChanges) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (changeType == null) {
			if (other.changeType != null)
				return false;
		} else if (!changeType.equals(other.changeType))
			return false;
		if (client == null) {
			if (other.client != null)
				return false;
		}
		if (clientType == null) {
			if (other.clientType != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedAt == null) {
			if (other.modifiedAt != null)
				return false;
		} else if (!modifiedAt.equals(other.modifiedAt))
			return false;
		if (modifiedBy == null) {
			if (other.modifiedBy != null)
				return false;
		}
		if (periodStart == null) {
			if (other.periodStart != null)
				return false;
		} else if (!periodStart.equals(other.periodStart))
			return false;
		return true;
	}

}