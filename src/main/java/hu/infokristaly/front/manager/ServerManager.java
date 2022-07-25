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

import hu.infokristaly.back.model.QrtzJobDetail;
import hu.infokristaly.middle.service.ServerInfoService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FileInfoLazyBean class.
 */
@SessionScoped
@Named
public class ServerManager implements Serializable {

    private static final long serialVersionUID = -4047835616860669800L;

    /** The log. */
    @Inject
    private Logger log;

    @Inject
    private ServerInfoService serverInfoService;

    /** The lazy data model. */
    private LazyDataModel<QrtzJobDetail> lazyDataModel;

    private Integer count = null;
    
    /**
     * Gets the lazy data model used for test lazy loaded PrimeFaces table.
     * 
     * @return the lazy data model
     */
    public LazyDataModel<QrtzJobDetail> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new LazyDataModel<QrtzJobDetail>() {

                private static final long serialVersionUID = 4047943852712178644L;
                
                private Map<String, Object> actualfilters;
                private String actualOrderField;
                private SortOrder actualSortOrder;

                @PostConstruct
                public void init() {
                    log.log(Level.INFO, "[ServerManager] constructor finished.");
                }

                @Override
                public QrtzJobDetail getRowData(String rowKey) {
                    Long primaryKey = Long.valueOf(rowKey);
                    return serverInfoService.find(primaryKey);
                }

                @Override
                public Object getRowKey(QrtzJobDetail entry) {
                    return entry.getId();
                }

                @Override
                public List<QrtzJobDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                    count=null;
                    this.setPageSize(pageSize);
                    List<QrtzJobDetail> result = (List<QrtzJobDetail>) serverInfoService.findRange(first, pageSize, this.actualOrderField, this.actualSortOrder, filters);
                    log.log(Level.INFO, "[ServerManager.LazyDataModel] load finished.");
                    return result;
                }

                @Override
                public int getRowCount() {
                    if (count == null) {
                        count = serverInfoService.count(actualfilters);
                    }
                    return count ;
                }

            };
        }
        return lazyDataModel;
    }
}
