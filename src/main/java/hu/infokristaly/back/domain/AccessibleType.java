package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Cacheable(value=true)
@Table(name="accessibletype")
@NamedQuery(name="AccessibleType.findAll", query="SELECT a FROM AccessibleType a")
public class AccessibleType implements Serializable {

    private static final long serialVersionUID = -1819079521899925869L;

    private Integer id;
	private String typename;

	public AccessibleType() {
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
	public String getTypename() {
		return this.typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((typename == null) ? 0 : typename.hashCode());
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
		AccessibleType other = (AccessibleType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (typename == null) {
			if (other.typename != null)
				return false;
		} else if (!typename.equals(other.typename))
			return false;
		return true;
	}

}