package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the qrtz_calendars database table.
 * 
 */
@Embeddable
public class QrtzCalendarPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String schedName;
	private String calendarName;

	public QrtzCalendarPK() {
	}

	@Column(name="sched_name", unique=true, nullable=false, length=120)
	public String getSchedName() {
		return this.schedName;
	}
	public void setSchedName(String schedName) {
		this.schedName = schedName;
	}

	@Column(name="calendar_name", unique=true, nullable=false, length=200)
	public String getCalendarName() {
		return this.calendarName;
	}
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof QrtzCalendarPK)) {
			return false;
		}
		QrtzCalendarPK castOther = (QrtzCalendarPK)other;
		return 
			this.schedName.equals(castOther.schedName)
			&& this.calendarName.equals(castOther.calendarName);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.schedName.hashCode();
		hash = hash * prime + this.calendarName.hashCode();
		
		return hash;
	}
}