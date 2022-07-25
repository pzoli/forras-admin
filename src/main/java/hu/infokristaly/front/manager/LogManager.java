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

import hu.infokristaly.back.model.LogEntry;
import hu.infokristaly.middle.service.LogService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class LogManager implements Serializable {


    private static final long serialVersionUID = -8317809641091956684L;

    /** The log. */
    @Inject
    private Logger log;

    @Inject
    private LogService logService;

    /** The lazy data model. */
    private LazyDataModel<LogEntry> lazyDataModel;

    private Integer count = null;
    
    private String loggedValue;

    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<LogEntry> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<LogEntry>() {

                private static final long serialVersionUID = 4047943852712178644L;
                
                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[LazyLogDataModel] constructor finished.");
                }

                @Override
                public LogEntry getRowData(String rowKey) {
                    Long primaryKey = Long.valueOf(rowKey);
                    return logService.find(primaryKey);
                }

                @Override
                public Object getRowKey(LogEntry entry) {
                    return entry.getId();
                }

                @Override
                public List<LogEntry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count=null;
                    this.setPageSize(pageSize);
                    if (loggedValue != null && !loggedValue.isEmpty()) {
                        filters.put("loggedValue", "%"+loggedValue+"%");
                    }
                    this.actualfilters = filters;
                    if (sortField != null) {
                        this.actualOrderField = sortField;
                    }
                    if (sortOrder != null) {
                        this.actualSortOrder = sortOrder;
                    }
                    List<LogEntry> result = (List<LogEntry>) logService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters);
                    log.log(Level.INFO, "[LazyDoctorDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = logService.count(actualfilters);
                    }
                    return count ;
                }

            };
        }
        return lazyDataModel;
    }

    public SelectItem[] getOptions() {
        final List<SelectItem> options = new ArrayList<SelectItem>();
        
        options.add(new SelectItem(null, "Üres"));
        options.add(new SelectItem(new Byte((byte) 1), "Létrehoz"));
        options.add(new SelectItem(new Byte((byte) 2), "Módosít"));
        options.add(new SelectItem(new Byte((byte) 3), "Töröl"));
        options.add(new SelectItem(new Byte((byte) 4), "Futás"));

        return options.toArray(new SelectItem[0]);
    }

    /**
     * @return the nySzam
     */
    public String getLoggedValue() {
        return loggedValue;
    }

    /**
     * @param nySzam the nySzam to set
     */
    public void setLoggedValue(String loggedValue) {
        this.loggedValue = loggedValue;
    }

    public boolean filterByDate(Object value, Object filter, Locale locale) {
        if( filter == null ) {
            return true;
        }

        if( value == null ) {
            return false;
        }

        return DateUtils.truncatedEquals((Date) filter, (Date) value, Calendar.DATE);
    }
    
    public void filterByNySzam() {
        lazyDataModel = null;
    }
}
