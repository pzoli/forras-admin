package hu.exprog.beecomposit.back.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class EmailImapParams {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String imapUser;
	private String imapPassword;
	private String imapServerHost;
	private String imapServerPort;
	@Version
	private Long version;

	
	public String getImapUser() {
		return imapUser;
	}
	public void setImapUser(String smtpUser) {
		this.imapUser = smtpUser;
	}
	public String getImapPassword() {
		return imapPassword;
	}
	public void setImapPassword(String smtpPassword) {
		this.imapPassword = smtpPassword;
	}
	public String getImapServerHost() {
		return imapServerHost;
	}
	public void setImapServerHost(String smtpServerHost) {
		this.imapServerHost = smtpServerHost;
	}
	public String getImapServerPort() {
		return imapServerPort;
	}
	public void setImapServerPort(String smtpServerPort) {
		this.imapServerPort = smtpServerPort;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imapPassword == null) ? 0 : imapPassword.hashCode());
		result = prime * result + ((imapServerHost == null) ? 0 : imapServerHost.hashCode());
		result = prime * result + ((imapServerPort == null) ? 0 : imapServerPort.hashCode());
		result = prime * result + ((imapUser == null) ? 0 : imapUser.hashCode());
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
		EmailImapParams other = (EmailImapParams) obj;
		if (imapPassword == null) {
			if (other.imapPassword != null)
				return false;
		} else if (!imapPassword.equals(other.imapPassword))
			return false;
		if (imapServerHost == null) {
			if (other.imapServerHost != null)
				return false;
		} else if (!imapServerHost.equals(other.imapServerHost))
			return false;
		if (imapServerPort == null) {
			if (other.imapServerPort != null)
				return false;
		} else if (!imapServerPort.equals(other.imapServerPort))
			return false;
		if (imapUser == null) {
			if (other.imapUser != null)
				return false;
		} else if (!imapUser.equals(other.imapUser))
			return false;
		return true;
	}
	
}
