package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the qrtz_job_details database table.
 * 
 */
@Entity
@Table(name="qrtz_job_details")
@NamedQuery(name="QrtzJobDetail.findAll", query="SELECT q FROM QrtzJobDetail q")
public class QrtzJobDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzJobDetailPK id;
	private String description;
	private Boolean isDurable;
	private Boolean isNonconcurrent;
	private Boolean isUpdateData;
	private String jobClassName;
	private byte[] jobData;
	private Boolean requestsRecovery;
	private List<QrtzTrigger> qrtzTriggers;

	public QrtzJobDetail() {
	}


	@EmbeddedId
	public QrtzJobDetailPK getId() {
		return this.id;
	}

	public void setId(QrtzJobDetailPK id) {
		this.id = id;
	}


	@Column(length=250)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Column(name="is_durable", nullable=false)
	public Boolean getIsDurable() {
		return this.isDurable;
	}

	public void setIsDurable(Boolean isDurable) {
		this.isDurable = isDurable;
	}


	@Column(name="is_nonconcurrent", nullable=false)
	public Boolean getIsNonconcurrent() {
		return this.isNonconcurrent;
	}

	public void setIsNonconcurrent(Boolean isNonconcurrent) {
		this.isNonconcurrent = isNonconcurrent;
	}


	@Column(name="is_update_data", nullable=false)
	public Boolean getIsUpdateData() {
		return this.isUpdateData;
	}

	public void setIsUpdateData(Boolean isUpdateData) {
		this.isUpdateData = isUpdateData;
	}


	@Column(name="job_class_name", nullable=false, length=250)
	public String getJobClassName() {
		return this.jobClassName;
	}

	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}


	@Column(name="job_data")
	public byte[] getJobData() {
		return this.jobData;
	}

	public void setJobData(byte[] jobData) {
		this.jobData = jobData;
	}


	@Column(name="requests_recovery", nullable=false)
	public Boolean getRequestsRecovery() {
		return this.requestsRecovery;
	}

	public void setRequestsRecovery(Boolean requestsRecovery) {
		this.requestsRecovery = requestsRecovery;
	}


	//bi-directional many-to-one association to QrtzTrigger
	@OneToMany(mappedBy="qrtzJobDetail")
	public List<QrtzTrigger> getQrtzTriggers() {
		return this.qrtzTriggers;
	}

	public void setQrtzTriggers(List<QrtzTrigger> qrtzTriggers) {
		this.qrtzTriggers = qrtzTriggers;
	}

	public QrtzTrigger addQrtzTrigger(QrtzTrigger qrtzTrigger) {
		getQrtzTriggers().add(qrtzTrigger);
		qrtzTrigger.setQrtzJobDetail(this);

		return qrtzTrigger;
	}

	public QrtzTrigger removeQrtzTrigger(QrtzTrigger qrtzTrigger) {
		getQrtzTriggers().remove(qrtzTrigger);
		qrtzTrigger.setQrtzJobDetail(null);

		return qrtzTrigger;
	}

}