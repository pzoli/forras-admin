package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

import hu.exprog.beecomposit.back.model.SystemUser;

import java.util.Date;


/**
 * The persistent class for the leader database table.
 * 
 */
@Entity
@Table(name="casemanagerforclients")
@NamedQuery(name="CaseManagerForClients.findAll", query="SELECT l FROM CaseManagerForClients l")
public class CaseManagerForClients implements Serializable {
    
    private static final long serialVersionUID = 3127382833353758177L;

    private Integer id;
	private SystemUser manager;
	private Client client;
	private Date validFrom;
	private Date validTo;
	
	public CaseManagerForClients() {
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

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="manager")
	public SystemUser getManager() {
		return this.manager;
	}

	public void setManager(SystemUser systemUser) {
		this.manager = systemUser;
	}

	@Temporal(TemporalType.DATE)
    public Date getValidFrom() {
		return validFrom;
	}


	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	@Temporal(TemporalType.DATE)
	public Date getValidTo() {
		return validTo;
	}


	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="client")
	public Client getClient() {
		return client;
	}


	public void setClient(Client client) {
		this.client = client;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((validFrom == null) ? 0 : validFrom.hashCode());
		result = prime * result + ((validTo == null) ? 0 : validTo.hashCode());
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
		CaseManagerForClients other = (CaseManagerForClients) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (manager == null) {
			if (other.manager != null)
				return false;
		}
		if (validFrom == null) {
			if (other.validFrom != null)
				return false;
		} else if (!validFrom.equals(other.validFrom))
			return false;
		if (validTo == null) {
			if (other.validTo != null)
				return false;
		} else if (!validTo.equals(other.validTo))
			return false;
		return true;
	}

}