package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

/**
 * Entity implementation class for Entity: RFIDCardSystemUser
 *
 */
@Entity(name="RFIDCardReader")
public class RFIDCardReader implements Serializable {

    private static final long serialVersionUID = -7661898665102382566L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
    private Long id;
    
    @EntityFieldInfo(info="Dátumtól", weight=1, required=true, editor="date")
    private Date periodStart;
    @EntityFieldInfo(info="Dátumig", weight=2, required=false, editor="date")
    private Date periodEnd;
    @EntityFieldInfo(info="Olvasó azonosító", weight=3, required=true, editor="txt")
	private String readerId;
    @EntityFieldInfo(info="Megjegyzés", weight=4, required=true, editor="textarea")
	private String comment;

	public RFIDCardReader() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   

	public Date getPeriodStart() {
		return this.periodStart;
	}

	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}   
	public Date getPeriodEnd() {
		return this.periodEnd;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}
    public String getReaderId() {
        return readerId;
    }
    public String getComment() {
        return comment;
    }
    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((periodEnd == null) ? 0 : periodEnd.hashCode());
        result = prime * result + ((periodStart == null) ? 0 : periodStart.hashCode());
        result = prime * result + ((readerId == null) ? 0 : readerId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RFIDCardReader))
            return false;
        RFIDCardReader other = (RFIDCardReader) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (periodEnd == null) {
            if (other.periodEnd != null)
                return false;
        } else if (!periodEnd.equals(other.periodEnd))
            return false;
        if (periodStart == null) {
            if (other.periodStart != null)
                return false;
        } else if (!periodStart.equals(other.periodStart))
            return false;
        if (readerId == null) {
            if (other.readerId != null)
                return false;
        } else if (!readerId.equals(other.readerId))
            return false;
        return true;
    }   
   
}
