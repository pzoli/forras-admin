package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qrtz_blob_triggers database table.
 * 
 */
@Embeddable
public class QrtzBlobTriggerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String schedName;
	private String triggerName;
	private String triggerGroup;

	public QrtzBlobTriggerPK() {
	}

	@Column(name="sched_name", insertable=false, updatable=false, unique=true, nullable=false, length=120)
	public String getSchedName() {
		return this.schedName;
	}
	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	@Column(name="trigger_name", insertable=false, updatable=false, unique=true, nullable=false, length=200)
	public String getTriggerName() {
		return this.triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Column(name="trigger_group", insertable=false, updatable=false, unique=true, nullable=false, length=200)
	public String getTriggerGroup() {
		return this.triggerGroup;
	}
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QrtzBlobTriggerPK)) {
			return false;
		}
		QrtzBlobTriggerPK castOther = (QrtzBlobTriggerPK)other;
		return 
			this.schedName.equals(castOther.schedName)
			&& this.triggerName.equals(castOther.triggerName)
			&& this.triggerGroup.equals(castOther.triggerGroup);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.schedName.hashCode();
		hash = hash * prime + this.triggerName.hashCode();
		hash = hash * prime + this.triggerGroup.hashCode();
		
		return hash;
	}
}