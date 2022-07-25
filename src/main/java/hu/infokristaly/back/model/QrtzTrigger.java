package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the qrtz_triggers database table.
 * 
 */
@Entity
@Table(name="qrtz_triggers")
@NamedQuery(name="QrtzTrigger.findAll", query="SELECT q FROM QrtzTrigger q")
public class QrtzTrigger implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzTriggerPK id;
	private String calendarName;
	private String description;
	private Long endTime;
	private byte[] jobData;
	private Integer misfireInstr;
	private Long nextFireTime;
	private Long prevFireTime;
	private Integer priority;
	private Long startTime;
	private String triggerState;
	private String triggerType;
	private List<QrtzBlobTrigger> qrtzBlobTriggers;
	private List<QrtzCronTrigger> qrtzCronTriggers;
	private List<QrtzSimpleTrigger> qrtzSimpleTriggers;
	private List<QrtzSimpropTrigger> qrtzSimpropTriggers;
	private QrtzJobDetail qrtzJobDetail;

	public QrtzTrigger() {
	}


	@EmbeddedId
	public QrtzTriggerPK getId() {
		return this.id;
	}

	public void setId(QrtzTriggerPK id) {
		this.id = id;
	}


	@Column(name="calendar_name", length=200)
	public String getCalendarName() {
		return this.calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}


	@Column(length=250)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Column(name="end_time")
	public Long getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}


	@Column(name="job_data")
	public byte[] getJobData() {
		return this.jobData;
	}

	public void setJobData(byte[] jobData) {
		this.jobData = jobData;
	}


	@Column(name="misfire_instr")
	public Integer getMisfireInstr() {
		return this.misfireInstr;
	}

	public void setMisfireInstr(Integer misfireInstr) {
		this.misfireInstr = misfireInstr;
	}


	@Column(name="next_fire_time")
	public Long getNextFireTime() {
		return this.nextFireTime;
	}

	public void setNextFireTime(Long nextFireTime) {
		this.nextFireTime = nextFireTime;
	}


	@Column(name="prev_fire_time")
	public Long getPrevFireTime() {
		return this.prevFireTime;
	}

	public void setPrevFireTime(Long prevFireTime) {
		this.prevFireTime = prevFireTime;
	}


	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	@Column(name="start_time", nullable=false)
	public Long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}


	@Column(name="trigger_state", nullable=false, length=16)
	public String getTriggerState() {
		return this.triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}


	@Column(name="trigger_type", nullable=false, length=8)
	public String getTriggerType() {
		return this.triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}


	//bi-directional many-to-one association to QrtzBlobTrigger
	@OneToMany(mappedBy="qrtzTrigger")
	public List<QrtzBlobTrigger> getQrtzBlobTriggers() {
		return this.qrtzBlobTriggers;
	}

	public void setQrtzBlobTriggers(List<QrtzBlobTrigger> qrtzBlobTriggers) {
		this.qrtzBlobTriggers = qrtzBlobTriggers;
	}

	public QrtzBlobTrigger addQrtzBlobTrigger(QrtzBlobTrigger qrtzBlobTrigger) {
		getQrtzBlobTriggers().add(qrtzBlobTrigger);
		qrtzBlobTrigger.setQrtzTrigger(this);

		return qrtzBlobTrigger;
	}

	public QrtzBlobTrigger removeQrtzBlobTrigger(QrtzBlobTrigger qrtzBlobTrigger) {
		getQrtzBlobTriggers().remove(qrtzBlobTrigger);
		qrtzBlobTrigger.setQrtzTrigger(null);

		return qrtzBlobTrigger;
	}


	//bi-directional many-to-one association to QrtzCronTrigger
	@OneToMany(mappedBy="qrtzTrigger")
	public List<QrtzCronTrigger> getQrtzCronTriggers() {
		return this.qrtzCronTriggers;
	}

	public void setQrtzCronTriggers(List<QrtzCronTrigger> qrtzCronTriggers) {
		this.qrtzCronTriggers = qrtzCronTriggers;
	}

	public QrtzCronTrigger addQrtzCronTrigger(QrtzCronTrigger qrtzCronTrigger) {
		getQrtzCronTriggers().add(qrtzCronTrigger);
		qrtzCronTrigger.setQrtzTrigger(this);

		return qrtzCronTrigger;
	}

	public QrtzCronTrigger removeQrtzCronTrigger(QrtzCronTrigger qrtzCronTrigger) {
		getQrtzCronTriggers().remove(qrtzCronTrigger);
		qrtzCronTrigger.setQrtzTrigger(null);

		return qrtzCronTrigger;
	}


	//bi-directional many-to-one association to QrtzSimpleTrigger
	@OneToMany(mappedBy="qrtzTrigger")
	public List<QrtzSimpleTrigger> getQrtzSimpleTriggers() {
		return this.qrtzSimpleTriggers;
	}

	public void setQrtzSimpleTriggers(List<QrtzSimpleTrigger> qrtzSimpleTriggers) {
		this.qrtzSimpleTriggers = qrtzSimpleTriggers;
	}

	public QrtzSimpleTrigger addQrtzSimpleTrigger(QrtzSimpleTrigger qrtzSimpleTrigger) {
		getQrtzSimpleTriggers().add(qrtzSimpleTrigger);
		qrtzSimpleTrigger.setQrtzTrigger(this);

		return qrtzSimpleTrigger;
	}

	public QrtzSimpleTrigger removeQrtzSimpleTrigger(QrtzSimpleTrigger qrtzSimpleTrigger) {
		getQrtzSimpleTriggers().remove(qrtzSimpleTrigger);
		qrtzSimpleTrigger.setQrtzTrigger(null);

		return qrtzSimpleTrigger;
	}


	//bi-directional many-to-one association to QrtzSimpropTrigger
	@OneToMany(mappedBy="qrtzTrigger")
	public List<QrtzSimpropTrigger> getQrtzSimpropTriggers() {
		return this.qrtzSimpropTriggers;
	}

	public void setQrtzSimpropTriggers(List<QrtzSimpropTrigger> qrtzSimpropTriggers) {
		this.qrtzSimpropTriggers = qrtzSimpropTriggers;
	}

	public QrtzSimpropTrigger addQrtzSimpropTrigger(QrtzSimpropTrigger qrtzSimpropTrigger) {
		getQrtzSimpropTriggers().add(qrtzSimpropTrigger);
		qrtzSimpropTrigger.setQrtzTrigger(this);

		return qrtzSimpropTrigger;
	}

	public QrtzSimpropTrigger removeQrtzSimpropTrigger(QrtzSimpropTrigger qrtzSimpropTrigger) {
		getQrtzSimpropTriggers().remove(qrtzSimpropTrigger);
		qrtzSimpropTrigger.setQrtzTrigger(null);

		return qrtzSimpropTrigger;
	}


	//bi-directional many-to-one association to QrtzJobDetail
	@ManyToOne
	public QrtzJobDetail getQrtzJobDetail() {
		return this.qrtzJobDetail;
	}

	public void setQrtzJobDetail(QrtzJobDetail qrtzJobDetail) {
		this.qrtzJobDetail = qrtzJobDetail;
	}

}