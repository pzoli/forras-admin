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

@Entity
@Cacheable(value = true)
@Table(name = "clerk")
public class Clerk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Long id;

    @Basic
    String name;

    @Basic
    String phone;

    @Basic
    @ManyToOne
    Organization organization;

}
