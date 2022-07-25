package hu.infokristaly.front.manager;

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.domain.EventHistory;
import hu.infokristaly.middle.service.ActivityService;
import hu.infokristaly.middle.service.ClientsService;
import hu.infokristaly.middle.service.ScheduleService;
import hu.infokristaly.utils.ClientComparatorByNySzam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
/*
import org.primefaces.component.timeline.DefaultTimelineUpdater;
import org.primefaces.component.timeline.Timeline;
import org.primefaces.component.timeline.TimelineUpdater;
import org.primefaces.event.timeline.TimelineLazyLoadEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
*/
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.SortOrder;

@SessionScoped
@Named
public class ActivityManager implements Serializable {

    private static final long serialVersionUID = -2812358566616597537L;

    @Inject
    private ActivityService activityService;

    @Inject
    private ClientsService clientService;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private Logger log;

    //private TimelineModel model;

    private long zoomMax;

    private Client currentClient;
    private Long currentClientId;
    private ClientChanges currentChange;
    private Date currentTime;
    private String filterNySzam;
    private Date visitorsTime;
    private LazyDataModel<EventHistory> lazyDataModel;
    private Integer lazyCount = null;
    private Integer visitorsCount;

    private LazyDataModel<Client> visitorsLazyData;

    private Boolean showActiveOnly = null;

    private final TimeZone timeZone = TimeZone.getTimeZone("Europe/Budapest");

    @PostConstruct
    protected void initialize() {
        // create empty model
        //model = new TimelineModel();

        // about five months in milliseconds for zoomMax
        // this can help to avoid a long loading of events when zooming out to
        // wide time ranges
        if (currentTime != null) {
            setZoomMax(1000L * 60 * 60 * 24 * 31);
        } else {
            setZoomMax(1000L * 60 * 60 * 24 * 31 * 5);
        }
    }

    public void update() {

    }

    public List<Client> findAllAliveClient() {
        if ((filterNySzam != null) && (!filterNySzam.endsWith("%"))) {
            filterNySzam += "%";
        }
        List<Client> result = activityService.findAllClient(null, true, filterNySzam);
        return result;
    }

    public List<Client> findAllClient() {
        if ((filterNySzam != null) && (!filterNySzam.endsWith("%"))) {
            filterNySzam += "%";
        }
        List<Client> result = activityService.findAllClient(showActiveOnly, false, filterNySzam);
        return result;
    }
/*
    private class MyTimelineUpdater extends DefaultTimelineUpdater {
        private static final long serialVersionUID = -2888773809249506119L;

        public MyTimelineUpdater(String id, String widgetVar) {
            super();
            this.id = id;
            this.setWidgetVar(widgetVar);
        }
    }
*/
    public void clearTable() {
        //clearTimeline();
    }

/*
    public void onLazyLoad(TimelineLazyLoadEvent e) {
        String id = ":mainform:timeline";
        // TimelineUpdater timelineUpdater = new
        // MyTimelineUpdater(id,"timelineWdgt");
        TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(id);

        Date startDate = e.getStartDateFirst();
        Date endDate = e.getEndDateFirst();
        clearTimeline();

        List<ScheduleEvent> events = activityService.loadEvents(startDate, endDate, timeZone, null, currentClient);
        for (ScheduleEvent event : events) {
            EventHistory eHistoryItem = ((EventHistory) event);
            String subjTypeId = "";
            if (eHistoryItem.getSubject() != null && (eHistoryItem.getSubject().getSubjectType() != null) && (eHistoryItem.getSubject().getSubjectType().getId() != null)) {
                subjTypeId = eHistoryItem.getSubject().getSubjectType().getId().toString();
            }
            if (timelineUpdater != null) {
                // timelineUpdater.add(new TimelineEvent(eHistoryItem.getTitle()
                // + "(" + subjTypeId + ")", event.getStartDate()));
                model.add(new TimelineEvent(eHistoryItem.getTitle() + "(" + subjTypeId + ")", event.getStartDate()), timelineUpdater);
            } else {
                model.add(new TimelineEvent(eHistoryItem.getTitle() + "(" + subjTypeId + ")", event.getStartDate()));
            }

        }

        if (e.hasTwoRanges()) {
            events = activityService.loadEvents(e.getStartDateSecond(), e.getEndDateSecond(), timeZone, null, currentClient);
            for (ScheduleEvent event : events) {
                if (timelineUpdater != null) {
                    model.add(new TimelineEvent(((EventHistory) event).getTitle(), e.getStartDateSecond()), timelineUpdater);
                } else {
                    model.add(new TimelineEvent(((EventHistory) event).getTitle(), e.getStartDateSecond()));
                }
            }
        }
    }
*/
    public void onClientChangeListener(AjaxBehaviorEvent event) {
        currentClient = new Client();
        if (currentClientId != null) {
            currentClient.setId(currentClientId);
            currentClient = clientService.find(currentClient);
        }
        //clearTimeline();
    }
/*
    public void clearTimeline() {
        model.clear();
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }
*/
    /**
     * @return the currentClient
     */
    public Client getCurrentClient() {
        return currentClient;
    }

    /**
     * @param currentClient
     *            the currentClient to set
     */
    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    /**
     * @return the zoomMax
     */
    public long getZoomMax() {
        return zoomMax;
    }

    /**
     * @param zoomMax
     *            the zoomMax to set
     */
    public void setZoomMax(long zoomMax) {
        this.zoomMax = zoomMax;
    }

    /**
     * @return the currentTime
     */
    public Date getCurrentTime() {
        return currentTime;
    }

    /**
     * @param currentTime
     *            the currentTime to set
     */
    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @return the filterNySzam
     */
    public String getFilterNySzam() {
        return filterNySzam;
    }

    /**
     * @param filterNySzam
     *            the filterNySzam to set
     */
    public void setFilterNySzam(String filterNySzam) {
        this.filterNySzam = filterNySzam;
    }

    /**
     * @return the visitorsTime
     */
    public Date getVisitorsTime() {
        return visitorsTime;
    }

    /**
     * @param visitorsTime
     *            the visitorsTime to set
     */
    public void setVisitorsTime(Date visitorsTime) {
        this.visitorsTime = visitorsTime;
    }

    /**
     * @return the clientService
     */
    public ClientsService getClientService() {
        return clientService;
    }

    /**
     * @param clientService
     *            the clientService to set
     */
    public void setClientService(ClientsService clientService) {
        this.clientService = clientService;
    }

    /**
     * @return the currentClientId
     */
    public Long getCurrentClientId() {
        return currentClientId;
    }

    /**
     * @param currentClientId
     *            the currentClientId to set
     */
    public void setCurrentClientId(Long currentClientId) {
        this.currentClientId = currentClientId;
    }

    public class Visite implements Serializable {

        private static final long serialVersionUID = 9190061740555396885L;

        private int id;
        private Client client;
        private ClientChanges change;

        public Visite() {

        }

        public Client getClient() {
            return client;
        }

        public ClientChanges getChange() {
            return change;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public void setChange(ClientChanges change) {
            this.change = change;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id
         *            the id to set
         */
        public void setId(int id) {
            this.id = id;
        }
    }

    public SelectItem[] getOptions() {
        final List<SelectItem> options = new ArrayList<SelectItem>();

        options.add(new SelectItem("", "Üres"));
        options.add(new SelectItem(Boolean.FALSE.toString(), "Nem"));
        options.add(new SelectItem(Boolean.TRUE.toString(), "Igen"));

        return options.toArray(new SelectItem[0]);
    }

    /**
     * @return the currentChange
     */
    public ClientChanges getCurrentChange() {
        return currentChange;
    }

    /**
     * @param currentChange
     *            the currentChange to set
     */
    public void setCurrentChange(ClientChanges currentChange) {
        this.currentChange = currentChange;
    }

    public String getCurrentChangeStyle(ClientChanges clientChange) {
        if ((currentChange != null) && (clientChange != null) && (clientChange.getId().equals(currentChange.getId()))) {
            return "font-weight: bold;";
        } else {
            return "";
        }
    }

    public List<Client> visitorsList() {
        List<Client> result = null;
        if (visitorsTime != null) {
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(visitorsTime);
            endDate.add(Calendar.DATE, 1);
            result = activityService.visitorsList(visitorsTime, endDate.getTime(), TimeZone.getDefault());
            Collections.sort(result, new ClientComparatorByNySzam());
        } else {
            result = null;
        }
        return result;
    }

    public List<ClientChanges> getClientChanges() {
        setCurrentChange(clientService.findClientChange(currentClient, visitorsTime));
        List<ClientChanges> changes = clientService.findClientAllChange(currentClient,false);
        return changes;
    }

    /**
     * @return the lazyDataModel
     */
    public LazyDataModel<EventHistory> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<EventHistory>() {
                private Map<String, Object> currentFilters = null;

                private static final long serialVersionUID = 7053078049861901989L;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyActivityDataModel] constructor finished.");
                }

                @Override
                public int getRowCount() {
                    if (lazyCount == null) {
                        if (currentFilters != null) {
                            lazyCount = activityService.getCount(currentFilters);
                        } else {
                            lazyCount = 0;
                        }
                    }
                    return lazyCount;
                }

                @Override
                public List<EventHistory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    List<EventHistory> result = new LinkedList<EventHistory>();
                    lazyCount = null;
                    if (currentClient != null) {
                        Date startDate = getCurrentTime();
                        if (startDate != null) {
                            filters.put("startDate", startDate);
                        }
                        filters.put("client", currentClient);
                        currentFilters = filters;
                        result = activityService.loadEvents(first, pageSize, sortField, sortOrder, filters);
                    }
                    return result;
                }

                @Override
                public EventHistory getRowData(String rowKey) {
                    EventHistory event = new EventHistory();
                    event.setEventId(Long.parseLong(rowKey));
                    return scheduleService.find(event);
                }

                @Override
                public Object getRowKey(EventHistory object) {
                    return object.getEventId();
                }

            };
        }
        return lazyDataModel;
    }

    public void setLazyDataModel(LazyDataModel<EventHistory> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }

    public LazyDataModel<Client> getVisitorsLazyData() {
        if (visitorsLazyData == null) {
            visitorsLazyData = new LazyDataModel<Client>() {
                private static final long serialVersionUID = 8295214268473407243L;

                private Map<String, Object> currentFilters = null;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[VisitorsLazyData] constructor finished.");
                }

                @Override
                public int getRowCount() {
                    if (visitorsCount == null) {
                        if (visitorsTime != null) {
                            Calendar endDate = Calendar.getInstance();
                            endDate.setTime(visitorsTime);
                            endDate.add(Calendar.DATE, 1);
                            visitorsCount = activityService.visitorCount(visitorsTime, endDate.getTime(), TimeZone.getDefault(), currentFilters);
                        } else {
                            visitorsCount = 0;
                        }
                    }
                    return visitorsCount;
                }

                @Override
                public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    List<Client> result = null;
                    if (visitorsTime != null) {
                        visitorsCount = null;
                        currentFilters = filters;
                        Calendar endDate = Calendar.getInstance();
                        endDate.setTime(visitorsTime);
                        endDate.add(Calendar.DATE, 1);
                        result = activityService.loadVisitorList(visitorsTime, endDate.getTime(), TimeZone.getDefault(), first, pageSize, sortField, sortOrder, filters);
                    } else {
                        visitorsCount = 0;
                    }
                    return result;
                }

                @Override
                public Client getRowData(String rowKey) {
                    Client result = new Client();
                    result.setId(Long.getLong(rowKey));
                    result = clientService.find(result);
                    return result;
                }

                @Override
                public Object getRowKey(Client client) {
                    Object result = client.getId();
                    return result;
                }

            };
        }
        return visitorsLazyData;
    }

    public void setVisitorsLazyData(LazyDataModel<Client> visitorsLazyData) {
        this.visitorsLazyData = visitorsLazyData;
    }

    public Boolean getShowActiveOnly() {
        return showActiveOnly;
    }

    public void setShowActiveOnly(Boolean showActiveOnly) {
        this.showActiveOnly = showActiveOnly;
    }

}
