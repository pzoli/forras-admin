package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

import hu.exprog.beecomposit.back.model.SystemUser;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the leader database table.
 * 
 */
@Entity
@Table(name="groupforclients")
@NamedQuery(name="GroupForClients.findAll", query="SELECT l FROM GroupForClients l order by l.name")
public class GroupForClients implements Serializable {

    private static final long serialVersionUID = 8669845509690010623L;

    private Integer id;
	private String name;
	private List<SystemUser> leaders;
	private List<Client> clients;
	private String subject;
	private Date validFrom;
	private Date validTo;
	private SystemUser createdBy;
	
	public GroupForClients() {
		
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


	@Column(nullable=false)
	@Basic
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "group_client", joinColumns = { @JoinColumn(name = "group_id", referencedColumnName="id") }, inverseJoinColumns = { @JoinColumn(name = "client_id", referencedColumnName="id") })
	public List<Client> getClients() {
		return this.clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}


	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_leaders", joinColumns = { @JoinColumn(name = "group_id", referencedColumnName="id") }, inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName="id") })
	public List<SystemUser> getLeaders() {
		return leaders;
	}


	public void setLeaders(List<SystemUser> leaders) {
		this.leaders = leaders;
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
	@JoinColumn(name="created_by")
	public SystemUser getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(SystemUser createdBy) {
		this.createdBy = createdBy;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		GroupForClients other = (GroupForClients) obj;
		if (clients == null) {
			if (other.clients != null)
				return false;
		}
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (leaders == null) {
			if (other.leaders != null)
				return false;
		}
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
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