package hu.exprog.beecomposit.back.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

@Entity
public class AlertAddresses {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Basic
	private Long id;
	@Basic
	@EntityFieldInfo(info="Név",editor="txt",weight=1,required=true)
	private String addresseName;
	@Basic
	@EntityFieldInfo(info="Telefonszám",editor="txt",weight=2,required=true)
	private String phone;
	@Basic
	@EntityFieldInfo(info="Engedélyezve",editor="booleancheckbox",weight=3)	
	private Boolean enabled;
	@Version
	private Long version;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAddresseName() {
		return addresseName;
	}
	public void setAddresseName(String name) {
		this.addresseName = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
		
}
