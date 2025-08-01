package hu.infokristaly.back.domain;

import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.FieldRightsInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;

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
	
    @EntityFieldInfo(info="RFID kártya felhasználó", weight=1, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="userName", detailDialogFile="/admin/rfidusers-dialog", filterField = "rfidCardUser.userName", sortField = "rfidCardUser.userName")
    @FieldRightsInfo(admin = "#{false}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfcarduserid")
    private RFIDCardUser rfidCardUser;
    
    @EntityFieldInfo(info="RFID kártyaolvasó", weight=2, required=true, editor="select")
    @LookupFieldInfo(keyField="id",labelField="comment", detailDialogFile="/admin/rfidreaders-dialog", filterField = "rfidCardReader.comment", sortField = "rfidCardReader.comment")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfcardid")
	private RFIDCardReader rfidCardReader;
	
	@EntityFieldInfo(info="Dátumt", weight=1, required=true, editor="datetime")
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
