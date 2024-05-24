/**
 *
 * http://www.ibstaff.net/fmartinez/?p=57
 *
 */
package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.back.domain.EventTemplate;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.EventTemplateService;
import hu.infokristaly.middle.service.GroupForClientsService;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.middle.service.SubjectService;
import hu.infokristaly.middle.service.UserService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.SortOrder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pzoli
 * 
 */
@Named
@SessionScoped
public class ScheduleManager implements Serializable {

	private static final long serialVersionUID = 9211439032365399563L;

	private EventHistory currentEvent = new EventHistory();

	private ScheduleModel lazyEventModel;

	private ScheduleModel lazyMeetingModel;

	private boolean showNYSzam = false;

	@Inject
	private UserService userService;

	@Inject
	private ScheduleService scheduleService;

	@Inject
	private ClientsService clientsService;

	@Inject
	private SubjectService subjectService;

	@Inject
	private EventTemplateService eventTemplateService;

	@Inject
	private GroupForClientsService groupForClientsService;

	@Inject
	private Logger log;

	private String scheduleView;

	private static String MEETINGSTYLECLASS = "meetingstyle";

	private List<Subject> subjects;

	private SystemUser[] lessionManagers = { };
	private Date initialDate = new Date();

	private DualListModel<SystemUser> userModel;
	private DualListModel<Client> clientsModel;

	private LazyDataModel<Client> lazyDataModel;
	private Client[] selectedSrcClients = {};
	private SystemUser[] selectedLeaders = {};

	public ScheduleModel getEventModel() {
		if (lazyEventModel == null) {
			lazyEventModel = new LazyScheduleModel() {

				private static final long serialVersionUID = -7030798462691388329L;

				@Override
				public void loadEvents(Date start, Date end) {
					Calendar init = new GregorianCalendar();
					init.setTime(start);
					int max = init.getActualMaximum(Calendar.DAY_OF_MONTH);
					int actual = init.get(Calendar.DAY_OF_MONTH);
					if ("month".equals(scheduleView) && init.get(Calendar.DAY_OF_MONTH) > 1) {
						init.add(Calendar.DATE, 7);
					}
					initialDate = init.getTime();
					System.out.println(scheduleView + " initial date: " + initialDate + " day of month (actual:"
							+ actual + ", max: " + max + ")");
					final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");
					List<ScheduleEvent> eventList = scheduleService.loadEvents(start, end, timeZone, false,
							lessionManagers);
					clear();
					for (ScheduleEvent event : eventList) {
						addEvent(event);
					}
				}

			};
		}
		return lazyEventModel;
	}

	public void cleanLazyMeetingModel() {
		lazyEventModel = null;
	}

	@PostConstruct
	private void init() {
		ArrayList<SystemUser> userList = new ArrayList<SystemUser>();
		ArrayList<Client> clientList = new ArrayList<Client>();
		clientsModel = new DualListModel<>(clientList, new ArrayList<Client>());
		userModel = new DualListModel<SystemUser>(userList, new ArrayList<SystemUser>());
	}

	public void onEventSelect(SelectEvent selectEvent) {
		ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();
		if (event != null) {
			currentEvent = (EventHistory) event;
			currentEvent = scheduleService.find(currentEvent);
			initialDate = event.getStartDate();
		}

		setSubjects(subjectService.findAllSubject(event.getStartDate()));

		setLeaders();
		setClients();
		selectedSrcClients = currentEvent.getClients().toArray(selectedSrcClients);
		selectedLeaders = currentEvent.getLeaders().toArray(selectedLeaders);
		setSelectedSrcClients(selectedSrcClients);
		resetTables();
	}

	public Date updateToLocaleDate(Date event) {
		final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");
		Calendar startDate = new GregorianCalendar();
		startDate.setTime(event);
		/*
		 * if (timeZone.inDaylightTime(event)) { startDate.add(Calendar.MILLISECOND,
		 * timeZone.getDSTSavings()); event = startDate.getTime(); }
		 */
		return event;
	}

	public void onDateSelect(SelectEvent selectEvent) {
		Date event = (Date) selectEvent.getObject();
		resetTables();
		event = updateToLocaleDate(event);
		initialDate = event;
		Calendar eCalendar = GregorianCalendar.getInstance();
		eCalendar.setTime(event);
		if (eCalendar.get(Calendar.HOUR_OF_DAY) < 8) {
			eCalendar.set(Calendar.HOUR_OF_DAY, 8);
			event = eCalendar.getTime();
		}

		Calendar startDate = new GregorianCalendar();
		startDate.setTime(event);

		Calendar endDate = new GregorianCalendar();
		endDate.setTime(event);
		endDate.add(Calendar.HOUR_OF_DAY, 1);

		currentEvent = new EventHistory();
		currentEvent.setStartDate(startDate.getTime());
		GroupForClients groupForClients = new GroupForClients();
		groupForClients.setId(-1);
		currentEvent.setGroupForClients(groupForClients);
		currentEvent.setEndDate(endDate.getTime());
		currentEvent.setEditable(true);
		setLeaders();
		setClients();
		selectedSrcClients = new Client[] {};
		selectedLeaders = new SystemUser[] { userService.getLoggedInSystemUser() };
	}

	private void resetTables() {
		UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
		DataTable pickList = (DataTable) root.findComponent("timerForm:eventTabs:pickList");
		if (pickList != null) {
			pickList.setFirst(0);
		}

		DataTable leaderPickList = (DataTable) root.findComponent("timerForm:eventTabs:leaderpickList");
		if (leaderPickList != null) {
			leaderPickList.setFirst(0);
		}

		DataTable selectedClients = (DataTable) root.findComponent("timerForm:selectedClientsTable");
		if (selectedClients != null) {
			selectedClients.setFirst(0);
		}

		DataTable selectedLeaders = (DataTable) root.findComponent("timerForm:selectedLeadersTable");
		if (selectedLeaders != null) {
			selectedLeaders.setFirst(0);
		}
	}

	public void onGroupChangeListener(ValueChangeEvent event) {
		lazyDataModel = null;
		currentEvent.setGroupForClients((GroupForClients) event.getNewValue());
		UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
		UIComponent pickList = root.findComponent("timerForm:eventTabs:pickList");
		if ((pickList != null) && (pickList instanceof DataTable)) {
			((DataTable) pickList).setFirst(0);
		}
		setClients();
	}

	public void onSubjectChangeListener(AjaxBehaviorEvent event) {
		if (currentEvent.getSubject() != null) {
			currentEvent.setTitle(currentEvent.getSubject().getTitle());
			setEndDateAfterStartDateChange(currentEvent.getStartDate());
		}
	}

	public void updateEndDate(SelectEvent e) {
		Date eStartDate = (Date) e.getObject();
		setEndDateAfterStartDateChange(eStartDate);
	}

	public void updateEndDateOnStartChange() {
		setEndDateAfterStartDateChange(currentEvent.getStartDate());
	}

	public void updateEndDateOnStartChange(SelectEvent event) {
		setEndDateAfterStartDateChange((Date) event.getObject());
	}

	public void setEndDateAfterStartDateChange(Date eStartDate) {
		if (currentEvent != null) {
			if (currentEvent.getSubject() != null) {
				Integer shiftInMinute = currentEvent.getSubject().getLenghtInMinute() == null ? 60
						: currentEvent.getSubject().getLenghtInMinute();
				Calendar end = GregorianCalendar.getInstance();
				end.setTime(eStartDate);
				end.add(Calendar.MINUTE, shiftInMinute);
				currentEvent.setEndDate(end.getTime());
			} else if (eStartDate != null) {
				Calendar end = GregorianCalendar.getInstance();
				end.setTime(eStartDate);
				end.add(Calendar.MINUTE, 60);
				currentEvent.setEndDate(end.getTime());
			}
		}
	}

	public void deleteCurrentEvent() {
		EventHistory[] removable = { currentEvent };
		scheduleService.delete(userService.getLoggedInSystemUser(), removable);
	}

	public void setClients() {
		if (currentEvent != null) {
			List<Client> clientsList = new ArrayList<Client>();
			if (currentEvent.getGroupForClients() != null) {
				if (currentEvent.getGroupForClients().getId() == -1) {
					clientsList = clientsService.findAll(true, null, currentEvent.getStartDate(), null);
				} else {
					clientsList = currentEvent.getGroupForClients().getClients();
				}
			} else {
				clientsList = clientsService.findAll(true, null, currentEvent.getStartDate(), null);
			}

			if ((currentEvent.getClients() == null) || (currentEvent.getClients().size() == 0)) {
				currentEvent.setClients(new ArrayList<Client>());
				setClientsModel(new DualListModel<Client>(clientsList, new ArrayList<Client>()));	
			} else {
				for (Client client : currentEvent.getClients()) {
					if (clientsList.contains(client)) {
						clientsList.remove(client);
					}
				}
				Collections.sort(currentEvent.getClients());
				setClientsModel(new DualListModel<Client>(clientsList, currentEvent.getClients()));
			}
		} else {
			setClientsModel(new DualListModel<Client>(new ArrayList<Client>(), new ArrayList<Client>()));
		}
	}

	private void setLeaders() {
		try {
			ArrayList<SystemUser> userList = new ArrayList<SystemUser>(userService.findAll(true));
			if ((currentEvent.getLeaders() == null) || (currentEvent.getLeaders().size() == 0)) {
				SystemUser user = userService.getLoggedInSystemUser();
				currentEvent.setLeaders(new ArrayList<SystemUser>());
				currentEvent.getLeaders().add(user);
				if (userList.contains(user)) {
					userList.remove(user);
				}
			} else {
				for (SystemUser user : currentEvent.getLeaders()) {
					if (userList.contains(user)) {
						userList.remove(user);
					}
				}
			}
			userModel = new DualListModel<SystemUser>(userList, currentEvent.getLeaders());
		} catch (Exception ex) {
			log.severe(ex.getLocalizedMessage());
		}
	}

	public void onEventMove(ScheduleEntryMoveEvent selectEvent) {
		EventHistory event = (EventHistory) selectEvent.getScheduleEvent();
		initialDate = event.getStartDate();
		event.setModificationDate(new Date());
		event.setModifiedBy(userService.getLoggedInSystemUser());
		currentEvent = event;
		scheduleService.mergeEvent(event);
	}

	public void onEventResize(ScheduleEntryResizeEvent selectEvent) {
		EventHistory event = (EventHistory) selectEvent.getScheduleEvent();
		event.setModificationDate(new Date());
		event.setModifiedBy(userService.getLoggedInSystemUser());
		initialDate = event.getStartDate();
		currentEvent = event;
		scheduleService.mergeEvent(event);
	}

	public EventHistory getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(EventHistory currentEvent) {
		this.currentEvent = currentEvent;
	}

	public void persistCurrentEventAndSelections(Boolean isMeeting) {
		if (currentEvent.getEventId() != null) {
			currentEvent.setModificationDate(new Date());
			currentEvent.setClients(null);
			currentEvent.setLeaders(null);
			currentEvent.setModifiedBy(userService.getLoggedInSystemUser());
			scheduleService.mergeEvent(currentEvent);
			currentEvent.setClients(Arrays.asList(selectedSrcClients));
			currentEvent.setLeaders(Arrays.asList(selectedLeaders));
			scheduleService.mergeEvent(currentEvent);
		} else {
			currentEvent.setCreatedDate(new Date());
			currentEvent.setCreatedBy(userService.getLoggedInSystemUser());
			if (isMeeting) {
				currentEvent.setStyleClass(MEETINGSTYLECLASS);
				Subject subject = new Subject();
				subject.setId(-1);
				currentEvent.setSubject(subject);
			}
			currentEvent.setClients(Arrays.asList(selectedSrcClients));
			currentEvent.setLeaders(Arrays.asList(selectedLeaders));
			scheduleService.persistEvent(currentEvent);
		}
	}

	public void persistCurrentEvent(Boolean isMeeting) {
		if (currentEvent.getEventId() != null) {
			currentEvent.setModificationDate(new Date());
			currentEvent.setModifiedBy(userService.getLoggedInSystemUser());
			scheduleService.mergeEvent(currentEvent);
		} else {
			currentEvent.setCreatedDate(new Date());
			currentEvent.setCreatedBy(userService.getLoggedInSystemUser());
			if (isMeeting) {
				currentEvent.setStyleClass(MEETINGSTYLECLASS);
				Subject subject = new Subject();
				subject.setId(-1);
				currentEvent.setSubject(subject);
			}
			scheduleService.persistEvent(currentEvent);
		}
	}

	@SuppressWarnings("unchecked")
	public void onTransferClient(TransferEvent event) {
		List<Client> clients = (List<Client>) event.getItems();
		Iterator<Client> iter = clients.iterator();
		if (event.isAdd()) {
			while (iter.hasNext()) {
				Client client = iter.next();
				currentEvent.getClients().add(client);
			}
		} else {
			while (iter.hasNext()) {
				Client client = iter.next();
				currentEvent.getClients().remove(client);
			}
		}
		Collections.sort(clients);
	}

	@SuppressWarnings("unchecked")
	public void onTransferClientIds(TransferEvent event) {
		List<String> clients = (List<String>) event.getItems();
		Iterator<String> iter = clients.iterator();
		if (event.isAdd()) {
			while (iter.hasNext()) {
				Client client = new Client();
				client.setId(Long.valueOf(iter.next()));
				client = clientsService.find(client);
				currentEvent.getClients().add(client);
			}
		} else {
			while (iter.hasNext()) {
				Client client = new Client();
				client.setId(Long.valueOf(iter.next()));
				client = clientsService.find(client);
				currentEvent.getClients().remove(client);
			}
		}
		Collections.sort(clients);
	}

	@SuppressWarnings("unchecked")
	public void onTransferLeader(TransferEvent event) {
		List<SystemUser> leaders = (List<SystemUser>) event.getItems();
		Iterator<SystemUser> iter = leaders.iterator();
		if (event.isAdd()) {
			while (iter.hasNext()) {
				SystemUser user = iter.next();
				currentEvent.getLeaders().add(user);
			}
		} else {
			while (iter.hasNext()) {
				SystemUser user = iter.next();
				currentEvent.getLeaders().remove(user);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void onTransferLeaderIds(TransferEvent event) {
		List<String> leaders = (List<String>) event.getItems();
		Iterator<String> iter = leaders.iterator();
		if (event.isAdd()) {
			while (iter.hasNext()) {
				SystemUser user = new SystemUser();
				user.setUserid(Long.valueOf(iter.next()));
				user = userService.find(user);
				currentEvent.getLeaders().add(user);
			}
		} else {
			while (iter.hasNext()) {
				SystemUser user = new SystemUser();
				user.setUserid(Long.valueOf(iter.next()));
				user = userService.find(user);
				currentEvent.getLeaders().remove(user);
			}
		}
	}

	public DualListModel<SystemUser> getUserModel() {
		return userModel;
	}

	public void setUserModel(DualListModel<SystemUser> userModel) {
		this.userModel = userModel;
	}

	public DualListModel<Client> getClientsModel() {
		return clientsModel;
	}

	public void setClientsModel(DualListModel<Client> clientsModel) {
		this.clientsModel = clientsModel;
	}

	public GroupForClientsService getGroupForClientsService() {
		return groupForClientsService;
	}

	public void setGroupForClientsService(GroupForClientsService groupForClientsService) {
		this.groupForClientsService = groupForClientsService;
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public SystemUser[] getLessionManagers() {
		if (lessionManagers.length == 0) {
			lessionManagers = new SystemUser[] {userService.getLoggedInSystemUser()};
		}
		return lessionManagers;
	}

	public void setLessionManagers(SystemUser[] lessionManagers) {
		this.lessionManagers = lessionManagers;
	}

	public ScheduleModel meetingModel() {
		if (lazyMeetingModel == null) {
			lazyMeetingModel = new LazyScheduleModel() {

				private static final long serialVersionUID = -4563281625057859552L;

				@Override
				public void loadEvents(Date start, Date end) {

					final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");
					List<ScheduleEvent> eventList = scheduleService.loadEvents(start, end, timeZone, true, null);
					clear();
					for (ScheduleEvent event : eventList) {
						addEvent(event);
					}
				}

			};
		}
		return lazyMeetingModel;
	}

	public void setLazyMeetingModel(ScheduleModel lazyMeetingModel) {
		this.lazyMeetingModel = lazyMeetingModel;
	}

	public void createTemplate(EventHistory event) {
		persistCurrentEvent(false);
		String result = "/user/event_templates.xhtml";
		EventTemplate template = new EventTemplate(event);
		template.setEnabled(false);
		template.setPeriod((byte) 1);
		Date now = new Date();
		Calendar startCalendarDate = Calendar.getInstance();
		startCalendarDate.setTimeZone(TimeZone.getDefault());
		startCalendarDate.setTime(now);
		if (event.getStartDate().before(now)) {
			startCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
			startCalendarDate.set(Calendar.MINUTE, 0);
			startCalendarDate.set(Calendar.SECOND, 0);
			startCalendarDate.set(Calendar.MILLISECOND, 0);
			int dow = startCalendarDate.get(Calendar.DAY_OF_WEEK);
			while ((dow == Calendar.SATURDAY) || (dow == Calendar.SUNDAY)) {
				startCalendarDate.add(Calendar.HOUR_OF_DAY, 24);
				dow = startCalendarDate.get(Calendar.DAY_OF_WEEK);
			}
			startCalendarDate.set(Calendar.HOUR_OF_DAY, 10);
			startCalendarDate.set(Calendar.MINUTE, 30);
			template.setPeriodStartDate(startCalendarDate.getTime());
		} else {
			template.setPeriodStartDate(event.getStartDate());
		}
		eventTemplateService.persistTemplate(template);

		FacesContext context = FacesContext.getCurrentInstance();
		String url = context.getApplication().getViewHandler().getActionURL(context, result);
		try {
			context.getExternalContext().redirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getScheduleView() {
		String result;
		if (scheduleView == null) {
			result = userService.isAdmin() ? "month" : "agendaWeek";
			scheduleView = result;
		} else {
			result = scheduleView;
		}
		return result;
	}

	public void onViewChange(SelectEvent selectEvent) {
		setScheduleView(selectEvent.getObject().toString());
	}

	public void setScheduleView(String value) {
		this.scheduleView = value;
	}

	/**
	 * @return the showNYSzam
	 */
	public boolean isShowNYSzam() {
		return showNYSzam;
	}

	public void addMessage() {
		String summary = showNYSzam ? "Checked" : "Unchecked";
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
	}

	/**
	 * @param showNYSzam the showNYSzam to set
	 */
	public void setShowNYSzam(boolean showNYSzam) {
		this.showNYSzam = showNYSzam;
	}

	/**
	 * @return the initialDate
	 */
	public Date getInitialDate() {
		return initialDate;
	}

	/**
	 * @param initialDate the initialDate to set
	 */
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public LazyDataModel<Client> getLazyDataModel() {
		if (lazyDataModel == null) {
			lazyDataModel = new LazyDataModel<Client>() {

				private static final long serialVersionUID = 1678907483750487431L;

				private Map<String, Object> actualfilters;

				private String actualOrderField;
				private SortOrder actualSortOrder;

				@PostConstruct
				public void init() {
					log.log(Level.INFO, "[LazyClientDataModel] constructor finished.");
				}

				@Override
				public Client getRowData(String rowKey) {
					Client client = new Client();
					client.setId(Long.valueOf(rowKey));
					return clientsService.find(client);
				}

				@Override
				public Object getRowKey(Client client) {
					if (client instanceof Client) {
						return client.getId();
					} else {
						return null;
					}
				}

				@Override
				public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder,
						Map<String, Object> filters) {
					this.setPageSize(pageSize);
					if (currentEvent.getGroupForClients() != null) {
						filters.put("clientgroup", currentEvent.getGroupForClients());
					}
					this.actualfilters = filters;
					if (sortField != null) {
						this.actualOrderField = sortField;
					}
					if (sortOrder != null) {
						this.actualSortOrder = sortOrder;
					}
					String[] clientTypes = {};
					List<Client> result = (List<Client>) clientsService.findRange(first, pageSize,
							this.actualOrderField, this.actualSortOrder, filters, true, null, null, clientTypes);
					log.log(Level.INFO, "[LazyClientDataModel] load finished.");
					return result;
				}

				@Override
				public int getRowCount() {
					String[] clientTypes = {};
					int result = clientsService.count(actualfilters, true, null, null, clientTypes);
					return result;
				}

			};
		}
		return lazyDataModel;
	}

	/**
	 * @return the selectedSrcClients
	 */
	public Client[] getSelectedSrcClients() {
		return selectedSrcClients;
	}

	/**
	 * @param selectedSrcClients the selectedSrcClients to set
	 */
	public void setSelectedSrcClients(Client[] selectedClients) {
		this.selectedSrcClients = selectedClients;
	}

	public void onRowSelect(SelectEvent event) {
		Client sClient = (Client) event.getObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Hozzáadás: " + sClient.getNeve()));
	}

	public void onRowUnselect(UnselectEvent event) {
		Client sClient = (Client) event.getObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Eltávolítás: " + sClient.getNeve()));
	}

	public void onLeaderRowSelect(SelectEvent event) {
		SystemUser sSystemUser = (SystemUser) event.getObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Hozzáadás: " + sSystemUser.getUsername()));
	}

	public void onLeaderRowUnselect(UnselectEvent event) {
		SystemUser sSystemUser = (SystemUser) event.getObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Eltávolítás: " + sSystemUser.getUsername()));
	}

	public void onToggleselect(ToggleSelectEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, new FacesMessage("Oldal kijelölve: " + (event.isSelected() ? "igen" : "nem")));
	}

	/**
	 * @return the selectedLeaders
	 */
	public SystemUser[] getSelectedLeaders() {
		return selectedLeaders;
	}

	/**
	 * @param selectedLeaders the selectedLeaders to set
	 */
	public void setSelectedLeaders(SystemUser[] selectedLeaders) {
		this.selectedLeaders = selectedLeaders;
	}

	/**
	 * @return the noneGroup
	 */
	public GroupForClients getNoneGroup() {
		GroupForClients noneGroup = new GroupForClients();
		noneGroup.setId(0);
		return noneGroup;
	}

}
