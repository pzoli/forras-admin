package hu.infokristaly.back.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


/**
 * The persistent class for the qrtz_simprop_triggers database table.
 * 
 */
@Entity
@Table(name="qrtz_simprop_triggers")
@NamedQuery(name="QrtzSimpropTrigger.findAll", query="SELECT q FROM QrtzSimpropTrigger q")
public class QrtzSimpropTrigger implements Serializable {
	private static final long serialVersionUID = 1L;
	private QrtzSimpropTriggerPK id;
	private Boolean boolProp1;
	private Boolean boolProp2;
	private BigDecimal decProp1;
	private BigDecimal decProp2;
	private Integer intProp1;
	private Integer intProp2;
	private Long longProp1;
	private Long longProp2;
	private String strProp1;
	private String strProp2;
	private String strProp3;
	private QrtzTrigger qrtzTrigger;

	public QrtzSimpropTrigger() {
	}


	@EmbeddedId
	public QrtzSimpropTriggerPK getId() {
		return this.id;
	}

	public void setId(QrtzSimpropTriggerPK id) {
		this.id = id;
	}


	@Column(name="bool_prop_1")
	public Boolean getBoolProp1() {
		return this.boolProp1;
	}

	public void setBoolProp1(Boolean boolProp1) {
		this.boolProp1 = boolProp1;
	}


	@Column(name="bool_prop_2")
	public Boolean getBoolProp2() {
		return this.boolProp2;
	}

	public void setBoolProp2(Boolean boolProp2) {
		this.boolProp2 = boolProp2;
	}


	@Column(name="dec_prop_1", precision=13, scale=4)
	public BigDecimal getDecProp1() {
		return this.decProp1;
	}

	public void setDecProp1(BigDecimal decProp1) {
		this.decProp1 = decProp1;
	}


	@Column(name="dec_prop_2", precision=13, scale=4)
	public BigDecimal getDecProp2() {
		return this.decProp2;
	}

	public void setDecProp2(BigDecimal decProp2) {
		this.decProp2 = decProp2;
	}


	@Column(name="int_prop_1")
	public Integer getIntProp1() {
		return this.intProp1;
	}

	public void setIntProp1(Integer intProp1) {
		this.intProp1 = intProp1;
	}


	@Column(name="int_prop_2")
	public Integer getIntProp2() {
		return this.intProp2;
	}

	public void setIntProp2(Integer intProp2) {
		this.intProp2 = intProp2;
	}


	@Column(name="long_prop_1")
	public Long getLongProp1() {
		return this.longProp1;
	}

	public void setLongProp1(Long longProp1) {
		this.longProp1 = longProp1;
	}


	@Column(name="long_prop_2")
	public Long getLongProp2() {
		return this.longProp2;
	}

	public void setLongProp2(Long longProp2) {
		this.longProp2 = longProp2;
	}


	@Column(name="str_prop_1", length=512)
	public String getStrProp1() {
		return this.strProp1;
	}

	public void setStrProp1(String strProp1) {
		this.strProp1 = strProp1;
	}


	@Column(name="str_prop_2", length=512)
	public String getStrProp2() {
		return this.strProp2;
	}

	public void setStrProp2(String strProp2) {
		this.strProp2 = strProp2;
	}


	@Column(name="str_prop_3", length=512)
	public String getStrProp3() {
		return this.strProp3;
	}

	public void setStrProp3(String strProp3) {
		this.strProp3 = strProp3;
	}


	//bi-directional many-to-one association to QrtzTrigger
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="sched_name", referencedColumnName="sched_name", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="trigger_group", referencedColumnName="trigger_group", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="trigger_name", referencedColumnName="trigger_name", nullable=false, insertable=false, updatable=false)
		})
	@ForeignKey(name = "qrtz_simprop_triggers_sched_name_fkey")
	public QrtzTrigger getQrtzTrigger() {
		return this.qrtzTrigger;
	}

	public void setQrtzTrigger(QrtzTrigger qrtzTrigger) {
		this.qrtzTrigger = qrtzTrigger;
	}

}