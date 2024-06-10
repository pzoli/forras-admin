package hu.infokristaly.back.domain;

import hu.infokristaly.back.domain.RFIDCardReader;
import hu.infokristaly.back.domain.RFIDCardUser;
import hu.infokristaly.front.annotations.EntityInfo;
import hu.infokristaly.front.annotations.LookupFieldInfo;

import java.io.Serializable;
import java.lang.Long;
import java.util.Date;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: RFIDLogEntry
 *
 */
@Entity

public class RFIDLogEntry implements Serializable {

    private static final long serialVersionUID = -5987903563774980633L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false)
	private Long id;
	
    @EntityInfo(info="RFID kártya felhasználó", weight=1, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="userName", detailDialogFile="/admin/rfidusers-dialog")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfcarduserid")
    private RFIDCardUser rfidCardUser;
    
    @EntityInfo(info="RFID kártyaolvasó", weight=2, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="comment", detailDialogFile="/admin/rfidreaders-dialog")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfcardid")
	private RFIDCardReader rfidCardReader;
	
	@EntityInfo(info="Dátumt", weight=1, required=true, editor="date")
	private Date logDate;
	

	public RFIDLogEntry() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public RFIDCardUser getRfidCardUser() {
		return this.rfidCardUser;
	}

	public void setRfidCardUser(RFIDCardUser rfidCardUser) {
		this.rfidCardUser = rfidCardUser;
	}   
	public RFIDCardReader getRfidCardReader() {
		return this.rfidCardReader;
	}

	public void setRfidCardReader(RFIDCardReader rfidCardReader) {
		this.rfidCardReader = rfidCardReader;
	}   
	public Date getLogDate() {
		return this.logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
   
}
