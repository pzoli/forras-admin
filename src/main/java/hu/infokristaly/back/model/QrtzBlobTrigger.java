package hu.infokristaly.back.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


/**
 * The persistent class for the qrtz_blob_triggers database table.
 * 
 */
@Entity
@Table(name="qrtz_blob_triggers")
@NamedQuery(name="QrtzBlobTrigger.findAll", query="SELECT q FROM QrtzBlobTrigger q")
public class QrtzBlobTrigger implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzBlobTriggerPK id;
	private byte[] blobData;
	private QrtzTrigger qrtzTrigger;

	public QrtzBlobTrigger() {
	}


	@EmbeddedId
	public QrtzBlobTriggerPK getId() {
		return this.id;
	}

	public void setId(QrtzBlobTriggerPK id) {
		this.id = id;
	}


	@Column(name="blob_data")
	public byte[] getBlobData() {
		return this.blobData;
	}

	public void setBlobData(byte[] blobData) {
		this.blobData = blobData;
	}


	//bi-directional many-to-one association to QrtzTrigger
	@ManyToOne
	@JoinColumns(value = {
		@JoinColumn(name="sched_name", referencedColumnName="sched_name", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="trigger_group", referencedColumnName="trigger_group", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="trigger_name", referencedColumnName="trigger_name", nullable=false, insertable=false, updatable=false)
		})
	@ForeignKey(name = "qrtz_blob_triggers_sched_name_fkey")
	public QrtzTrigger getQrtzTrigger() {
		return this.qrtzTrigger;
	}

	public void setQrtzTrigger(QrtzTrigger qrtzTrigger) {
		this.qrtzTrigger = qrtzTrigger;
	}

}