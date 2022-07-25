package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the qrtz_scheduler_state database table.
 * 
 */
@Entity
@Table(name="qrtz_scheduler_state")
@NamedQuery(name="QrtzSchedulerState.findAll", query="SELECT q FROM QrtzSchedulerState q")
public class QrtzSchedulerState implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzSchedulerStatePK id;
	private Long checkinInterval;
	private Long lastCheckinTime;

	public QrtzSchedulerState() {
	}


	@EmbeddedId
	public QrtzSchedulerStatePK getId() {
		return this.id;
	}

	public void setId(QrtzSchedulerStatePK id) {
		this.id = id;
	}


	@Column(name="checkin_interval", nullable=false)
	public Long getCheckinInterval() {
		return this.checkinInterval;
	}

	public void setCheckinInterval(Long checkinInterval) {
		this.checkinInterval = checkinInterval;
	}


	@Column(name="last_checkin_time", nullable=false)
	public Long getLastCheckinTime() {
		return this.lastCheckinTime;
	}

	public void setLastCheckinTime(Long lastCheckinTime) {
		this.lastCheckinTime = lastCheckinTime;
	}

}