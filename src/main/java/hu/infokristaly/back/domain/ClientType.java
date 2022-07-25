package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "client_type")
@Cacheable(value=true)
@NamedQuery(name = "ClientType.findAll", query = "SELECT a FROM ClientType a")
public class ClientType implements Serializable {

    private static final long serialVersionUID = 6661983434712372792L;

    private Integer id;
    private String typename;

    public ClientType() {
    }

    public ClientType(int id) {
        setId(id);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
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
		ClientType other = (ClientType) obj;
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