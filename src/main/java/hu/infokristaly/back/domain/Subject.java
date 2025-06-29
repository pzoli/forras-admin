package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;

@Entity
@Cacheable(value=true)
@Table(name="subject")
@NamedQuery(name="Subject.findAll", query="SELECT a FROM Subject a")
public class Subject implements Serializable {

    private static final long serialVersionUID = -1021160915376137624L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;
    
    @EntityFieldInfo(info="Foglalkozás neve", weight=1, required=true, editor="txt")
    @Basic
    private String title;
    
    @EntityFieldInfo(info="Egyéni foglalkozás", weight=2, required=true, editor="booleancheckbox")
    @Basic
    private Boolean uniqueMeeting;
    
    @EntityFieldInfo(info="Foglalkozás hossza", weight=3, required=true, editor="integer")
    @Basic
    private Integer lenghtInMinute;
    
    @EntityFieldInfo(info="Foglalkozás jellege", weight=4, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="name")
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="subject_type")
    private SubjectType subjectType;

    @EntityFieldInfo(info="Figyelmeztetés nullázó", weight=5, required=false, editor="booleancheckbox")
    @Basic
    private Boolean resetAlert;

    @EntityFieldInfo(info="Megjegyzés", weight=6, required=false, editor="txt")
    @Basic
    private String comment;
    
    @EntityFieldInfo(info="Törölve", weight=7, required=false, editor="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleteDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getUniqueMeeting() {
        return uniqueMeeting;
    }

    public void setUniqueMeeting(Boolean uniqueMeeting) {
        this.uniqueMeeting = uniqueMeeting;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public Boolean getResetAlert() {
        return resetAlert;
    }

    public void setResetAlert(Boolean resetAlert) {
        this.resetAlert = resetAlert;
    }

    /**
     * @return the lenghtInMinute
     */
    public Integer getLenghtInMinute() {
        return lenghtInMinute;
    }

    /**
     * @param lenghtInMinute the lenghtInMinute to set
     */
    public void setLenghtInMinute(Integer lenghtInMinute) {
        this.lenghtInMinute = lenghtInMinute;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((deleteDate == null) ? 0 : deleteDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lenghtInMinute == null) ? 0 : lenghtInMinute.hashCode());
		result = prime * result + ((resetAlert == null) ? 0 : resetAlert.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uniqueMeeting == null) ? 0 : uniqueMeeting.hashCode());
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
		Subject other = (Subject) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (deleteDate == null) {
			if (other.deleteDate != null)
				return false;
		} else if (!deleteDate.equals(other.deleteDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lenghtInMinute == null) {
			if (other.lenghtInMinute != null)
				return false;
		} else if (!lenghtInMinute.equals(other.lenghtInMinute))
			return false;
		if (resetAlert == null) {
			if (other.resetAlert != null)
				return false;
		} else if (!resetAlert.equals(other.resetAlert))
			return false;
		if (subjectType == null) {
			if (other.subjectType != null)
				return false;
		}
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (uniqueMeeting == null) {
			if (other.uniqueMeeting != null)
				return false;
		} else if (!uniqueMeeting.equals(other.uniqueMeeting))
			return false;
		return true;
	}    
}
