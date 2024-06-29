package hu.infokristaly.middle.service;

import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.ClientType;
import hu.infokristaly.back.domain.Message;
import hu.exprog.beecomposit.back.model.SystemUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.SortOrder;

@Stateless
@Named
public class MessageService implements Serializable {

    private static final long serialVersionUID = 5171597252594999255L;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Message> findMessages(Date start, Date end, TimeZone timeZone, SystemUser systemUser) {
        List<Message> eventList = null;
        if (systemUser != null) {
            Calendar startDate = new GregorianCalendar();
            startDate.setTime(start);
            Calendar endDate = new GregorianCalendar();
            endDate.setTime(end);
            if (timeZone.inDaylightTime(start)) {
                startDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
                endDate.add(Calendar.MILLISECOND, timeZone.getDSTSavings() * -1);
            }

            String select = "select m from Messages m join fetch m.cards c where m.sentDate between :beginDate and :endDate and c.recipientSystemUser = :systemUser order by m.sentDate desc";

            Query q = em.createQuery(select);

            q.setParameter("beginDate", startDate.getTime());
            q.setParameter("endDate", endDate.getTime());
            q.setParameter("systemUser", systemUser);
            eventList = (List<Message>) q.getResultList();
        } else {
            eventList = new ArrayList<Message>();
        }
        return eventList;
    }

    public List<Message> findMessages(SystemUser systemUser) {
        Query q = em.createQuery("select m from Message m join fetch m.cards c where c.recipientSystemUser = :systemUser order by c.id desc");
        q.setParameter("systemUser", systemUser);
        List<Message> list = q.getResultList();
        return list;
    }

    public Message find(Message message) {
        Message result = em.find(Message.class, message.getId());
        return result;
    }

    public List<Message> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Query q = em.createQuery("select m from Message m join fetch m.cards c where c.recipientSystemUser = :systemUser order by c.id desc");
        SystemUser systemUser = (SystemUser) filters.get("systemUser");
        q.setParameter("systemUser", systemUser);
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        List<Message> list = q.getResultList();
        return list;
    }

    public List<Card> findCardRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Query q = em.createQuery("select c from Card c join fetch c.message m where c.recipientSystemUser = :systemUser order by c.id desc");
        SystemUser systemUser = (SystemUser) filters.get("systemUser");
        q.setParameter("systemUser", systemUser);
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        List<Card> list = q.getResultList();
        return list;
    }

    public List<Message> findSentRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Query q = em.createQuery("select m from Message m where m.sender = :systemUser order by m.id desc");
        SystemUser systemUser = (SystemUser) filters.get("systemUser");
        q.setParameter("systemUser", systemUser);
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        List<Message> list = q.getResultList();
        return list;
    }

    public int count(Map<String, Object> actualfilters) {
        String queryStr = "select count(c) from Card c where c.recipientSystemUser = :systemUser"; 
        Query q = em.createQuery(queryStr);
        int result = 0;
        if (actualfilters != null) {
            if (actualfilters.containsKey("systemUser")) {
                SystemUser systemUser = (SystemUser) actualfilters.get("systemUser");
                if (systemUser != null) {
                    q.setParameter("systemUser", systemUser);                    
                    result = ((Long)(q.getSingleResult())).intValue();
                }
            }
        }
        return result;
    }

    public int countMessage(Map<String, Object> actualfilters) {
        String queryStr = "select count(m) from Message m join fetch m.cards c where c.recipientSystemUser = :systemUser"; 
        Query q = em.createQuery(queryStr);
        int result = 0;
        if (actualfilters != null) {
            if (actualfilters.containsKey("systemUser")) {
                SystemUser systemUser = (SystemUser) actualfilters.get("systemUser");
                if (systemUser != null) {
                    q.setParameter("systemUser", systemUser);
                    result = (Integer) q.getSingleResult();
                    
                }
            }
        }
        return result;
    }

    public int countUnreded(Map<String, Object> actualfilters) {
        String queryStr = "select count(c) from Card c where c.recipientSystemUser = :systemUser and c.receivedDate is null"; 
        Query q = em.createQuery(queryStr);
        Integer result = 0;
        if (actualfilters != null) {
            if (actualfilters.containsKey("systemUser")) {
                SystemUser systemUser = (SystemUser) actualfilters.get("systemUser");
                if (systemUser != null) {
                    q.setParameter("systemUser", systemUser);
                    result = ((Long)(q.getSingleResult())).intValue();
                }
            }
        }
        return result;
    }

    public int sentCount(Map<String, Object> actualfilters) {
        Query q = em.createQuery("select count(m) from Message m where m.sender = :systemUser");
        Integer result = 0;
        if (actualfilters != null && actualfilters.containsKey("systemUser")) {
            SystemUser systemUser = (SystemUser) actualfilters.get("systemUser");
            q.setParameter("systemUser", systemUser);
            result = ((Long)(q.getSingleResult())).intValue();
        }
        return result;
    }

    public void merge(Message message) {
        em.merge(message);
    }

    public void delete(Card card) {
        Card result = em.find(Card.class, card.getId());
        Message msg = result.getMessage();
        msg.getCards().remove(result);
        em.merge(msg);        
        em.remove(result);
    }
    
    public void delete(Message message, SystemUser user) {
        Message msg = em.find(Message.class, message.getId());
        if ((msg != null) && (msg.getCards() != null)) {
            Iterator<Card> iter = msg.getCards().iterator();
            List<Card> removables = new LinkedList<Card>();
            while (iter.hasNext()) {
                Card item = iter.next();
                if (item.getRecipientSystemUser().getId() == user.getId()) {
                    removables.add(item);
                }
            }
            if (removables.size() > 0) {
                for (Card card : removables) {
                    msg.getCards().remove(card);
                }
                em.merge(msg);
            }
        }

    }

    public void delete(Message message) {
        Message msg = em.find(Message.class, message.getId());
        em.remove(msg);
    }

    public void persist(Message message) {
        em.persist(message);
        message.getCards().stream().forEach(c -> {
            c.setMessage(message);
            em.persist(c);
        });
    }

    public void merge(Card card) {
        em.merge(card);
    }

    public Card find(Card card) {
        Card result = em.find(Card.class, card.getId());
        return result;
    }
}
