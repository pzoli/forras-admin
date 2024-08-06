package hu.infokristaly.back.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import hu.exprog.beecomposit.back.model.Organization;
import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;

enum DocumentDirection {
    IN, OUT
}

@Entity
@Cacheable(value = true)
@Table(name = "doc_info")
public class DocInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Long id;

    @EntityFieldInfo(info="#{msg['document-subject']}", weight=2, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="value", detailDialogFile="/admin/document_subject-dialog", service="SubjectService", filterField="subject.value", sortField="subject.value")
    @Basic
    @ManyToOne
    DocumentSubject subject;

    @Basic
    DocumentDirection direction = DocumentDirection.IN;
    
    @EntityFieldInfo(info="#{msg['created_at']}", weight=1, required=true, editor="date")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @EntityFieldInfo(info="#{msg['organization']}", weight=3, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="name", detailDialogFile="/admin/organization-dialog", service="OrganizationService", filterField="organization.name", sortField="organization.name")
    @Basic
    @ManyToOne
    Organization organization;

    @Basic
    @ManyToOne
    Clerk clerk;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "docInfo", cascade = CascadeType.REMOVE)
    Collection<FileInfo> fileInfos;
    
	public Collection<FileInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(Collection<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentSubject getSubject() {
		return subject;
	}

	public void setSubject(DocumentSubject subject) {
		this.subject = subject;
	}

	public DocumentDirection getDirection() {
		return direction;
	}

	public void setDirection(DocumentDirection direction) {
		this.direction = direction;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Clerk getClerk() {
		return clerk;
	}

	public void setClerk(Clerk clerk) {
		this.clerk = clerk;
	}

	@Override
	public int hashCode() {
		return Objects.hash(clerk, createdAt, direction, id, organization, subject);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocInfo other = (DocInfo) obj;
		return Objects.equals(clerk, other.clerk) && Objects.equals(createdAt, other.createdAt)
				&& direction == other.direction && Objects.equals(id, other.id)
				&& Objects.equals(organization, other.organization) && Objects.equals(subject, other.subject);
	}

    
}
