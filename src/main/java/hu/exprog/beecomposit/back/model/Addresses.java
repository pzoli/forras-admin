package hu.exprog.beecomposit.back.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

@Entity
public class Addresses {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
	private Long id;
	@EntityFieldInfo(info="#{msg['pobox']}", weight=1, required=true, editor="txt")
	private String poBox;
	@EntityFieldInfo(info="#{msg['address']}", weight=1, required=true, editor="txt")
	private String address;
	
	@Version
	private Long version;
	
	public String getPoBox() {
		return poBox;
	}
	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((poBox == null) ? 0 : poBox.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Addresses other = (Addresses) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (poBox == null) {
			if (other.poBox != null)
				return false;
		} else if (!poBox.equals(other.poBox))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}
