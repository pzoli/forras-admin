package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;

/**
 * Entity implementation class for Entity: RFIDCard
 *
 */
@Entity

public class RFIDCard implements Serializable {

    private static final long serialVersionUID = -7298755312824692742L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
	private Long id;
    
    @EntityFieldInfo(info="Kártya azonosító", weight=1, required=true, editor="txt")
    @Basic
	private String rfid;
    
    @EntityFieldInfo(info="Kártya típus", weight=2, required=true, editor="txt")
    @Basic
	private String type;

	public RFIDCard() {
		super();
	}   

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getRfid() {
		return this.rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}   
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((rfid == null) ? 0 : rfid.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RFIDCard))
            return false;
        RFIDCard other = (RFIDCard) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (rfid == null) {
            if (other.rfid != null)
                return false;
        } else if (!rfid.equals(other.rfid))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
   
}
