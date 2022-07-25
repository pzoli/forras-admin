package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the cartaker database table.
 * 
 */
@Entity
@Table(name="cartaker")
@NamedQuery(name="Cartaker.findAll", query="SELECT c FROM Cartaker c")
public class Cartaker implements Serializable {
    
    private static final long serialVersionUID = 7181278164173166040L;

    private Integer id;
	private String name;
	private List<Accessible> acc;

	public Cartaker() {
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


	@Column
	@Basic
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "cartaker_client", joinColumns = { @JoinColumn(name = "cartaker_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "accessible_id", referencedColumnName = "id") })
	public List<Accessible> getAcc() {
		return acc;
	}


	public void setAcc(List<Accessible> acc) {
		this.acc = acc;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Cartaker other = (Cartaker) obj;
		if (acc == null) {
			if (other.acc != null)
				return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}