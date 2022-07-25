package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qrtz_paused_trigger_grps database table.
 * 
 */
@Embeddable
public class QrtzPausedTriggerGrpPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String schedName;
	private String triggerGroup;

	public QrtzPausedTriggerGrpPK() {
	}

	@Column(name="sched_name", unique=true, nullable=false, length=120)
	public String getSchedName() {
		return this.schedName;
	}
	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	@Column(name="trigger_group", unique=true, nullable=false, length=200)
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
		if (!(other instanceof QrtzPausedTriggerGrpPK)) {
			return false;
		}
		QrtzPausedTriggerGrpPK castOther = (QrtzPausedTriggerGrpPK)other;
		return 
			this.schedName.equals(castOther.schedName)
			&& this.triggerGroup.equals(castOther.triggerGroup);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.schedName.hashCode();
		hash = hash * prime + this.triggerGroup.hashCode();
		
		return hash;
	}
}