package hu.infokristaly.back.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the qrtz_calendars database table.
 * 
 */
@Entity
@Table(name="qrtz_calendars")
@NamedQuery(name="QrtzCalendar.findAll", query="SELECT q FROM QrtzCalendar q")
public class QrtzCalendar implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzCalendarPK id;
	private byte[] calendar;

	public QrtzCalendar() {
	}


	@EmbeddedId
	public QrtzCalendarPK getId() {
		return this.id;
	}

	public void setId(QrtzCalendarPK id) {
		this.id = id;
	}


	@Column(nullable=false)
	public byte[] getCalendar() {
		return this.calendar;
	}

	public void setCalendar(byte[] calendar) {
		this.calendar = calendar;
	}

}