package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qrtz_job_details database table.
 * 
 */
@Embeddable
public class QrtzJobDetailPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String schedName;
	private String jobName;
	private String jobGroup;

	public QrtzJobDetailPK() {
	}

	@Column(name="sched_name", unique=true, nullable=false, length=120)
	public String getSchedName() {
		return this.schedName;
	}
	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	@Column(name="job_name", unique=true, nullable=false, length=200)
	public String getJobName() {
		return this.jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Column(name="job_group", unique=true, nullable=false, length=200)
	public String getJobGroup() {
		return this.jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QrtzJobDetailPK)) {
			return false;
		}
		QrtzJobDetailPK castOther = (QrtzJobDetailPK)other;
		return 
			this.schedName.equals(castOther.schedName)
			&& this.jobName.equals(castOther.jobName)
			&& this.jobGroup.equals(castOther.jobGroup);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.schedName.hashCode();
		hash = hash * prime + this.jobName.hashCode();
		hash = hash * prime + this.jobGroup.hashCode();
		
		return hash;
	}
}