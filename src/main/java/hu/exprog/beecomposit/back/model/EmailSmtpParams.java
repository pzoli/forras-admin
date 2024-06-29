package hu.exprog.beecomposit.back.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class EmailSmtpParams {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String senderName;
	private String senderEmailAddress;
	private String smtpUser;
	private String smtpPassword;
	private String smtpServerHost;
	private String smtpServerPort;
	@Version
	private Long version;

	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderEmailAddress() {
		return senderEmailAddress;
	}
	public void setSenderEmailAddress(String senderEmailAddress) {
		this.senderEmailAddress = senderEmailAddress;
	}
	public String getSmtpUser() {
		return smtpUser;
	}
	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}
	public String getSmtpPassword() {
		return smtpPassword;
	}
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
	public String getSmtpServerHost() {
		return smtpServerHost;
	}
	public void setSmtpServerHost(String smtpServerHost) {
		this.smtpServerHost = smtpServerHost;
	}
	public String getSmtpServerPort() {
		return smtpServerPort;
	}
	public void setSmtpServerPort(String smtpServerPort) {
		this.smtpServerPort = smtpServerPort;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((senderEmailAddress == null) ? 0 : senderEmailAddress.hashCode());
		result = prime * result + ((senderName == null) ? 0 : senderName.hashCode());
		result = prime * result + ((smtpPassword == null) ? 0 : smtpPassword.hashCode());
		result = prime * result + ((smtpServerHost == null) ? 0 : smtpServerHost.hashCode());
		result = prime * result + ((smtpServerPort == null) ? 0 : smtpServerPort.hashCode());
		result = prime * result + ((smtpUser == null) ? 0 : smtpUser.hashCode());
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
		EmailSmtpParams other = (EmailSmtpParams) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (senderEmailAddress == null) {
			if (other.senderEmailAddress != null)
				return false;
		} else if (!senderEmailAddress.equals(other.senderEmailAddress))
			return false;
		if (senderName == null) {
			if (other.senderName != null)
				return false;
		} else if (!senderName.equals(other.senderName))
			return false;
		if (smtpPassword == null) {
			if (other.smtpPassword != null)
				return false;
		} else if (!smtpPassword.equals(other.smtpPassword))
			return false;
		if (smtpServerHost == null) {
			if (other.smtpServerHost != null)
				return false;
		} else if (!smtpServerHost.equals(other.smtpServerHost))
			return false;
		if (smtpServerPort == null) {
			if (other.smtpServerPort != null)
				return false;
		} else if (!smtpServerPort.equals(other.smtpServerPort))
			return false;
		if (smtpUser == null) {
			if (other.smtpUser != null)
				return false;
		} else if (!smtpUser.equals(other.smtpUser))
			return false;
		return true;
	}
	
}
