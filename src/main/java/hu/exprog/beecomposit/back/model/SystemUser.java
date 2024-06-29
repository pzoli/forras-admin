package hu.exprog.beecomposit.back.model;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.Basic;

// Generated 2012.09.26. 11:18:58 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import hu.exprog.honeyweb.front.annotations.DynamicMirror;
import hu.exprog.honeyweb.front.annotations.EntityFieldInfo;
import hu.exprog.honeyweb.front.annotations.FieldEntitySpecificRightsInfo;
import hu.exprog.honeyweb.front.annotations.FieldRightsInfo;
import hu.exprog.honeyweb.front.annotations.LookupFieldInfo;
import hu.exprog.honeyweb.front.annotations.QueryFieldInfo;
import hu.exprog.honeyweb.front.annotations.QueryInfo;

@Entity
@Table(name = "systemuser", schema = "public")
@DynamicMirror(queryInfoArray=@QueryInfo(queryName="SelfManager", fields = { @QueryFieldInfo(fieldName="osUserName",disabled=true), @QueryFieldInfo(fieldName="userName"), @QueryFieldInfo(fieldName="language",admin=false), @QueryFieldInfo(fieldName="osUserPassword")}))
public class SystemUser implements java.io.Serializable {

	private static final long serialVersionUID = 4103231713043595259L;

	private Long id;
	@EntityFieldInfo(info = "#{msg['address-email']}", weight = 3, required = true, editor = "email")
	private String osUserName;
	private Organizationunit organizationunit;
	@EntityFieldInfo(info = "#{msg['user-name']}", weight = 1, editor = "txt")
	private String userName;
	private String vCard;
	private byte[] photo;
	@EntityFieldInfo(info = "#{msg['sqlserver-login-name']}", weight = 1, editor = "txt")
	private String sqlserverloginname;
	@EntityFieldInfo(info = "#{msg['user-group']}", weight = 6, required = true, editor = "select")
	@LookupFieldInfo(keyField = "id", labelField = "usergroup", detailDialogFile = "/admin/usergroup-dialog", filterField = "usergroup", sortField = "usergroup")
	@FieldRightsInfo(admin="#{authBackingBean.checkDeveloperRights()}")
	@FieldEntitySpecificRightsInfo(disabled="#{authBackingBean.isEditorReadOnly()}")
	private Usergroup usergroup;
	@EntityFieldInfo(info = "#{msg['password']}", weight = 2, editor = "password", listable = false, postProcess = true)
	private String osUserPassword;
	@EntityFieldInfo(info = "#{msg['enabled']}", weight = 4, editor = "booleancheckbox")
	private Boolean enabled;
	@EntityFieldInfo(info = "#{msg['comment']}", weight = 5, editor = "txt")
	private String comment;
	@EntityFieldInfo(info = "#{msg['language']}", weight = 6, editor = "select")
	@LookupFieldInfo(keyField = "id", labelField = "language", detailDialogFile = "/admin/language-dialog", filterField = "language", sortField = "language")
	private Language language;
    @Basic
    @Column(name="emailaddress")
    private String emailAddress;
    @Column(name="casemanager")
    @Basic
    private Boolean caseManager = false;
    @Basic
    @Column(name="adminuser")
    private boolean adminUser = false;
    @Basic
    @Column(name="pincode")
    private String pinCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="deleteddate")
    private Date deletedDate;
	@Version
	private Long version;

	public SystemUser() {
	}

	public SystemUser(Long id) {
		this.id = id;
	}

	public SystemUser(Long id, String userName, String osUserName, Organizationunit organizationunit, String vCard, byte[] photo, String sqlserverloginname, Usergroup usergroup) {
		this.id = id;
		this.userName = userName;
		this.osUserName = osUserName;
		this.organizationunit = organizationunit;
		this.vCard = vCard;
		this.photo = photo;
		this.sqlserverloginname = sqlserverloginname;
		this.usergroup = usergroup;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "osusername", length = 128)
	public String getOsUserName() {
		return this.osUserName;
	}

	public void setOsUserName(String osusername) {
		this.osUserName = osusername;
	}

	@ManyToOne
	@JoinColumn(name = "organizationunit")
	public Organizationunit getOrganizationunit() {
		return this.organizationunit;
	}

	public void setOrganizationunit(Organizationunit organizationunit) {
		this.organizationunit = organizationunit;
	}

	@Column(name = "vcard")
	@Lob
	public String getVCard() {
		return this.vCard;
	}

	public void setVCard(String vCard) {
		this.vCard = vCard;
	}

	@Column(name = "photo")
	@Lob
	public byte[] getPhoto() {
		return this.photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	@Column(name = "sqlserverloginname", length = 50, unique = true)
	public String getSqlserverloginname() {
		return this.sqlserverloginname;
	}

	public void setSqlserverloginname(String sqlserverloginname) {
		this.sqlserverloginname = sqlserverloginname;
	}

	@ManyToOne
	@JoinColumn(name = "usergroup")
	public Usergroup getUsergroup() {
		return this.usergroup;
	}

	public void setUsergroup(Usergroup usergroup) {
		this.usergroup = usergroup;
	}

	@Column(name = "osuserpassword")
	public String getOsUserPassword() {
		return osUserPassword;
	}

	public void setOsUserPassword(String osUserPassword) {
		this.osUserPassword = osUserPassword;
	}

	@Column(name = "enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ManyToOne
	@JoinColumn(name = "language")
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Boolean getCaseManager() {
		return caseManager;
	}

	public void setCaseManager(Boolean caseManager) {
		this.caseManager = caseManager;
	}

	public boolean isAdminUser() {
		return adminUser;
	}

	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((organizationunit == null) ? 0 : organizationunit.hashCode());
		result = prime * result + ((osUserName == null) ? 0 : osUserName.hashCode());
		result = prime * result + ((osUserPassword == null) ? 0 : osUserPassword.hashCode());
		result = prime * result + Arrays.hashCode(photo);
		result = prime * result + ((sqlserverloginname == null) ? 0 : sqlserverloginname.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((usergroup == null) ? 0 : usergroup.hashCode());
		result = prime * result + ((vCard == null) ? 0 : vCard.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemUser other = (SystemUser) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (organizationunit == null) {
			if (other.organizationunit != null)
				return false;
		} else if (!organizationunit.equals(other.organizationunit))
			return false;
		if (osUserName == null) {
			if (other.osUserName != null)
				return false;
		} else if (!osUserName.equals(other.osUserName))
			return false;
		if (osUserPassword == null) {
			if (other.osUserPassword != null)
				return false;
		} else if (!osUserPassword.equals(other.osUserPassword))
			return false;
		if (!Arrays.equals(photo, other.photo))
			return false;
		if (sqlserverloginname == null) {
			if (other.sqlserverloginname != null)
				return false;
		} else if (!sqlserverloginname.equals(other.sqlserverloginname))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (usergroup == null) {
			if (other.usergroup != null)
				return false;
		} else if (!usergroup.equals(other.usergroup))
			return false;
		if (vCard == null) {
			if (other.vCard != null)
				return false;
		} else if (!vCard.equals(other.vCard))
			return false;
		return true;
	}

}
