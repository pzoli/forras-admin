package hu.infokristaly.back.domain;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import hu.exprog.beecomposit.back.model.Organization;
import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;

@Entity
@Cacheable(value = true)
@Table(name = "clerk")
public class Clerk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Long id;

    @EntityFieldInfo(info="#{msg['clerk-name']}", weight=1, required=true, editor="txt")
    @Basic
    String name;

    @EntityFieldInfo(info="#{msg['clerk-phone']}", weight=2, required=false, editor="txt")
    @Basic
    String phone;

    @EntityFieldInfo(info="#{msg['clerk-organization']}", weight=3, required=false, editor="select")
    @LookupFieldInfo(keyField="id",labelField="name", detailDialogFile="/admin/organization-dialog")
    @Basic
    @ManyToOne
    Organization organization;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, organization, phone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clerk other = (Clerk) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(organization, other.organization) && Objects.equals(phone, other.phone);
	}

}
