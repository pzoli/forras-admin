package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import hu.infokristaly.back.model.SystemUser;

/**
 * Entity implementation class for Entity: Message
 *
 */
@Entity
public class Message implements Serializable {
	
    private static final long serialVersionUID = -2199557575538002013L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Type(type = "org.hibernate.type.TextType")
    @Lob
    @Column(columnDefinition="text")
    private String message;
    
    @Basic
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender")
    private SystemUser sender;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE,CascadeType.MERGE,CascadeType.PERSIST})    
    @JoinTable(name = "message_card", joinColumns = { @JoinColumn(name = "message_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "card_id", referencedColumnName = "id") })
    private List<Card> cards;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="sent_date")
    private Date sentDate;

    @Basic
    private Boolean deliveredAll;
    
    public Message() {
		super();
	}

    public Long getId() {
        return id;
    }
    
    public String getMessage() {
        return message;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SystemUser getSender() {
        return sender;
    }

    public void setSender(SystemUser sender) {
        this.sender = sender;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setSentDate(Date receivedDate) {
        this.sentDate = receivedDate;
    }

    public Boolean getDeliveredAll() {
        return deliveredAll;
    }

    public void setDeliveredAll(Boolean deliveredAll) {
        this.deliveredAll = deliveredAll;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deliveredAll == null) ? 0 : deliveredAll.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((sentDate == null) ? 0 : sentDate.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Message other = (Message) obj;
		if (cards == null) {
			if (other.cards != null)
				return false;
		}
		if (deliveredAll == null) {
			if (other.deliveredAll != null)
				return false;
		} else if (!deliveredAll.equals(other.deliveredAll))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		}
		if (sentDate == null) {
			if (other.sentDate != null)
				return false;
		} else if (!sentDate.equals(other.sentDate))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
