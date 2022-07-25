/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Zoltan Papp
 * 
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Card;
import hu.infokristaly.back.domain.Message;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.middle.service.MessageService;
import hu.infokristaly.middle.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class MessageManager implements Serializable {

    private static final long serialVersionUID = -8140234913414827077L;

    /** The log. */
    @Inject
    private Logger log;

    /** The file info service. */
    @Inject
    private UserService userService;

    @Inject
    private MessageService messageService;

    private LazyDataModel<Card> lazyDataModel;

    private Integer receivedCount = null;

    private LazyDataModel<Message> lazyDataModelForSent;

    private Integer sentCount = null;

    private Card[] selectedCards = {};

    private Message[] selectedSentMessages = {};

    private SystemUser[] mailRecipients = {};

    private Message newMessage;

    @PostConstruct
    public void init() {

    }

    public LazyDataModel<Message> getLazyDataModelForSent() {
        if (lazyDataModelForSent == null) {
            lazyDataModelForSent = new LazyDataModel<Message>() {

                private static final long serialVersionUID = -7060145572783088084L;
                private Map<String, Object> actualfilters;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyMessageDataModel] constructor finished.");
                }

                @Override
                public Message getRowData(String rowKey) {
                    Message message = new Message();
                    message.setId(Long.valueOf(rowKey));
                    return messageService.find(message);
                }

                @Override
                public Object getRowKey(Message message) {
                    return message.getId();
                }

                @Override
                public List<Message> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    sentCount = null;
                    this.setPageSize(pageSize);
                    filters.put("systemUser", userService.getLoggedInSystemUser());
                    this.actualfilters = filters;
                    List<Message> result = (List<Message>) messageService.findSentRange(first, pageSize, sortField, sortOrder, filters);
                    log.log(Level.INFO, "[LazyMessageDataModel] load sent finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (sentCount == null) {
                        sentCount = messageService.sentCount(actualfilters);
                    }
                    return sentCount;
                }

            };
        }
        return lazyDataModelForSent;
    }

    /**
     * @return the lazyDataModel
     */
    public LazyDataModel<Card> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<Card>() {

                private static final long serialVersionUID = -7060145572783088084L;
                private Map<String, Object> actualfilters;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyCardDataModel] constructor finished.");
                }

                @Override
                public Card getRowData(String rowKey) {
                    Card card = new Card();
                    card.setId(Long.valueOf(rowKey));
                    return messageService.find(card);
                }

                @Override
                public Object getRowKey(Card message) {
                    return message.getId();
                }

                @Override
                public List<Card> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    receivedCount = null;
                    this.setPageSize(pageSize);
                    filters.put("systemUser", userService.getLoggedInSystemUser());
                    this.actualfilters = filters;
                    List<Card> result = (List<Card>) messageService.findCardRange(first, pageSize, sortField, sortOrder, filters);
                    log.log(Level.INFO, "[LazyMessageDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (receivedCount == null) {
                        receivedCount = messageService.count(actualfilters);
                    }
                    return receivedCount;
                }

            };
        }
        return lazyDataModel;
    }

    /**
     * @param lazyDataModel
     *            the lazyDataModel to set
     */
    public void setLazyDataModel(LazyDataModel<Card> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }

    /**
     * @return the selectedCards
     */
    public Card[] getSelectedCards() {
        return selectedCards;
    }

    /**
     * @param selectedMessages
     *            the selectedMessages to set
     */
    public void setSelectedCards(Card[] selectedCards) {
        this.selectedCards = selectedCards;
    }

    /**
     * @return the newMessage
     */
    public Message getNewMessage() {
        return newMessage;
    }

    /**
     * @param newMessage
     *            the newMessage to set
     */
    public void setNewMessage(Message newMessage) {
        this.newMessage = newMessage;
    }

    /**
     * @return the mailRecipients
     */
    public SystemUser[] getMailRecipients() {
        return mailRecipients;
    }

    /**
     * @param mailRecipients
     *            the mailRecipients to set
     */
    public void setMailRecipients(SystemUser[] mailRecipients) {
        this.mailRecipients = mailRecipients;
    }

    /**
     * @return the selectedSentMessages
     */
    public Message[] getSelectedSentMessages() {
        return selectedSentMessages;
    }

    /**
     * @param selectedSentMessages
     *            the selectedSentMessages to set
     */
    public void setSelectedSentMessages(Message[] selectedSentMessages) {
        this.selectedSentMessages = selectedSentMessages;
    }

    public void createNewMessage() {
        newMessage = new Message();
        newMessage.setSender(userService.getLoggedInSystemUser());
    }

    public void saveNewMessage() {
        if ((newMessage.getTitle() != null) && !newMessage.getTitle().isEmpty()) {
            newMessage.setCards(new LinkedList<Card>());
            newMessage.setMessage("<p style='font-size=26px;'>" + newMessage.getMessage() + "</p>");
            for (SystemUser recipient : mailRecipients) {
                Card card = new Card();
                if (recipient == null) {
                    recipient = userService.getLoggedInSystemUser();
                }
                card.setRecipientSystemUser(recipient);
                newMessage.getCards().add(card);
            }
            newMessage.setSentDate(new Date());
            messageService.persist(newMessage);
        }
    }

    public void onEdit(RowEditEvent event) {
        Message message = (Message) event.getObject();
        FacesMessage msg = new FacesMessage("Üzenet tárgya átszerkesztve", message.getTitle());
        messageService.merge(message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Módosítása visszavonva", ((Message) event.getObject()).getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onSentEdit(RowEditEvent event) {
        Message message = (Message) event.getObject();
        FacesMessage msg = new FacesMessage("Elküldött üzenet tárgya átszerkesztve", message.getTitle());
        messageService.merge(message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onSentCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Módosítása visszavonva", ((Message) event.getObject()).getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void delete() {
        SystemUser user = userService.getLoggedInSystemUser();
        for (Card card : selectedCards) {
            messageService.delete(card);
        }
    }

    public void deleteSelectedSentMessage() {
        for (Message msg : selectedSentMessages) {
            messageService.delete(msg);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        event.getTab().getTitle().equals("Beérkező üzenetek");
    }

    public void throwException() {
        throw new ViewExpiredException();
    }

    protected Card getMyCard(Message m) {
        Card result = null;
        SystemUser user = userService.getLoggedInSystemUser();
        for (Card card : m.getCards()) {
            if (card.getRecipientSystemUser().getUserid() == user.getUserid()) {
                result = card;
                break;
            }
        }
        return result;
    }

    public String getMessageTitleStyle(Message m) {
        String result = "font-weight:none;color: black;";
        if (m != null) {
            Card card = getMyCard(m);
            if (card != null) {
                if (card.getReceivedDate() == null) {
                    result = "font-weight:bold;color: red;";
                }
            }
        }
        return result;
    }

    public String getCardTitleStyle(Card card) {
        String result = ((card != null) && (card.getReceivedDate() == null)) ? "font-weight:bold;color: red;" : "font-weight:none;color: black;";
        return result;
    }

    public void onToggle(ToggleEvent event) {
        if (event.getVisibility().equals(Visibility.HIDDEN)) {
            Card card = ((Card) event.getData());
            card.setReceivedDate(new Date());
            messageService.merge(card);
            FacesMessage msg = new FacesMessage("Megtekintve", "Üzenet megtekintve");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void setReceivedDateToNow() {
        if ((selectedCards != null) && (selectedCards.length > 0)) {
            for (Card card : selectedCards) {
                card.setReceivedDate(new Date());
                messageService.merge(card);
            }            
            FacesMessage msg = new FacesMessage("Megtekintve", selectedCards.length + " üzenet megtekintve");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            selectedCards = new Card[]{};
        }
    }

    public String getSelectedMessageBody() {
        StringBuffer resultBuff = new StringBuffer();
        return resultBuff.toString();
    }
}
