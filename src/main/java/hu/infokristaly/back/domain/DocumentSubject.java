package hu.infokristaly.back.domain;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

@Entity
@Cacheable(value = true)
@Table(name = "document_subject")
public class DocumentSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Long id;

    @EntityFieldInfo(info="#{msg['document-subject']}", weight=1, required=true, editor="txt")
    @Basic
    String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentSubject other = (DocumentSubject) obj;
		return Objects.equals(id, other.id) && Objects.equals(value, other.value);
	}

}
