package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the qrtz_fired_triggers database table.
 * 
 */
@Entity
@Table(name="qrtz_fired_triggers")
@NamedQuery(name="QrtzFiredTrigger.findAll", query="SELECT q FROM QrtzFiredTrigger q")
public class QrtzFiredTrigger implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzFiredTriggerPK id;
	private Long firedTime;
	private String instanceName;
	private Boolean isNonconcurrent;
	private String jobGroup;
	private String jobName;
	private Integer priority;
	private Boolean requestsRecovery;
	private Long schedTime;
	private String state;
	private String triggerGroup;
	private String triggerName;

	public QrtzFiredTrigger() {
	}


	@EmbeddedId
	public QrtzFiredTriggerPK getId() {
		return this.id;
	}

	public void setId(QrtzFiredTriggerPK id) {
		this.id = id;
	}


	@Column(name="fired_time", nullable=false)
	public Long getFiredTime() {
		return this.firedTime;
	}

	public void setFiredTime(Long firedTime) {
		this.firedTime = firedTime;
	}


	@Column(name="instance_name", nullable=false, length=200)
	public String getInstanceName() {
		return this.instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}


	@Column(name="is_nonconcurrent")
	public Boolean getIsNonconcurrent() {
		return this.isNonconcurrent;
	}

	public void setIsNonconcurrent(Boolean isNonconcurrent) {
		this.isNonconcurrent = isNonconcurrent;
	}


	@Column(name="job_group", length=200)
	public String getJobGroup() {
		return this.jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}


	@Column(name="job_name", length=200)
	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	@Column(nullable=false)
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	@Column(name="requests_recovery")
	public Boolean getRequestsRecovery() {
		return this.requestsRecovery;
	}

	public void setRequestsRecovery(Boolean requestsRecovery) {
		this.requestsRecovery = requestsRecovery;
	}


	@Column(name="sched_time", nullable=false)
	public Long getSchedTime() {
		return this.schedTime;
	}

	public void setSchedTime(Long schedTime) {
		this.schedTime = schedTime;
	}


	@Column(nullable=false, length=16)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}


	@Column(name="trigger_group", nullable=false, length=200)
	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}


	@Column(name="trigger_name", nullable=false, length=200)
	public String getTriggerName() {
		return this.triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

}