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

    @Basic
    @ManyToOne
    DocumentSubject subject;

    @Basic
    DocumentDirection direction;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @Basic
    @ManyToOne
    Organization organization;

    @Basic
    @ManyToOne
    Clerk clerk;

}
