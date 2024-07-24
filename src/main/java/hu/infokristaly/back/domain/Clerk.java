package hu.infokristaly.back.domain;

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

}
