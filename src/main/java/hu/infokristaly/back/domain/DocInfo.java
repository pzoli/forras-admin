package hu.infokristaly.back.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @EntityFieldInfo(info="#{msg['document-subject']}", weight=2, required=false, editor="select")
    @LookupFieldInfo(keyField="id",labelField="name", detailDialogFile="/admin/organization-dialog")
    @Basic
    @ManyToOne
    DocumentSubject subject;

    @Basic
    DocumentDirection direction;
    
    @EntityFieldInfo(info="#{msg['created_at']}", weight=1, required=false, editor="date")
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @EntityFieldInfo(info="#{msg['organization']}", weight=3, required=false, editor="select")
    @LookupFieldInfo(keyField="id",labelField="name", detailDialogFile="/admin/organization-dialog")
    @Basic
    @ManyToOne
    Organization organization;

    @Basic
    @ManyToOne
    Clerk clerk;

}
