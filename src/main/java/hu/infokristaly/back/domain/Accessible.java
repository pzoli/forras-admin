package hu.infokristaly.back.domain;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "accessible")
@NamedQuery(name = "Accessible.findAll", query = "SELECT a FROM Accessible a")
public class Accessible implements Serializable {

    private static final long serialVersionUID = -236080079995839428L;

    private Integer id;
    private String accessibleValue;
    private AccessibleType accessible_type;

    public Accessible() {
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

    @Column(name = "accessible_value")
    public String getAccessibleValue() {
        return this.accessibleValue;
    }

    public void setAccessibleValue(String accessibleValue) {
        this.accessibleValue = accessibleValue;
    }

    @ManyToOne
    @JoinColumn(name = "accessible_type_id")
    public AccessibleType getAccessible_type() {
        return this.accessible_type;
    }

    public void setAccessible_type(AccessibleType accessible_type) {
        this.accessible_type = accessible_type;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessibleValue == null) ? 0 : accessibleValue.hashCode());
		result = prime * result + ((accessible_type == null) ? 0 : accessible_type.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Accessible other = (Accessible) obj;
		if (accessibleValue == null) {
			if (other.accessibleValue != null)
				return false;
		} else if (!accessibleValue.equals(other.accessibleValue))
			return false;
		if (accessible_type == null) {
			if (other.accessible_type != null)
				return false;
		} else if (!accessible_type.equals(other.accessible_type))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}