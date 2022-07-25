package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qrtz_locks database table.
 * 
 */
@Embeddable
public class QrtzLockPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String schedName;
	private String lockName;

	public QrtzLockPK() {
	}

	@Column(name="sched_name", unique=true, nullable=false, length=120)
	public String getSchedName() {
		return this.schedName;
	}
	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	@Column(name="lock_name", unique=true, nullable=false, length=40)
	public String getLockName() {
		return this.lockName;
	}
	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QrtzLockPK)) {
			return false;
		}
		QrtzLockPK castOther = (QrtzLockPK)other;
		return 
			this.schedName.equals(castOther.schedName)
			&& this.lockName.equals(castOther.lockName);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.schedName.hashCode();
		hash = hash * prime + this.lockName.hashCode();
		
		return hash;
	}
}