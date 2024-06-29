package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import hu.exprog.beecomposit.back.model.SystemUser;

/**
 * Entity implementation class for Entity: Recipient
 *
 */
@Entity
public class Card implements Serializable {

    private static final long serialVersionUID = -7136972459440362567L;

    public Card() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private SystemUser recipientSystemUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageId")
    private Message message;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "received_date")
    private Date receivedDate;

    public Long getId() {
        return id;
    }

    public SystemUser getRecipientSystemUser() {
        return recipientSystemUser;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setRecipientSystemUser(SystemUser recipientSystemUser) {
        this.recipientSystemUser = recipientSystemUser;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((receivedDate == null) ? 0 : receivedDate.hashCode());
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
		Card other = (Card) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		}
		if (receivedDate == null) {
			if (other.receivedDate != null)
				return false;
		} else if (!receivedDate.equals(other.receivedDate))
			return false;
		if (recipientSystemUser == null) {
			if (other.recipientSystemUser != null)
				return false;
		}
		return true;
	}
    
}
