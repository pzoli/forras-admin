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
package hu.infokristaly.middle.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.SortOrder;

import hu.infokristaly.back.domain.Alert;
import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.ClientChanges;
import hu.infokristaly.back.model.SystemUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.io.Serializable;

@Stateless
@Named
public class AlertService implements Serializable {

    private static final long serialVersionUID = 428306737581238709L;

    public static final int ALERT_DAY = 25;
    public static final int BACKALERT_DAY = 30;
    
    /** The logger. */
    @Inject
    private Logger log;

    /** The entity manager. */
    @Inject
    private EntityManager em;

    private Map<String, Object> currentFilters;
    private int currentCount;

    public AlertService() {

    }

    @PostConstruct
    public void init() {
    }

    /**
     * Find the fileInfo.
     * 
     * @param fileInfo
     *            the file info
     * @return the file info
     */
    public Alert find(Alert alert) {
        log.info("Find by Id [" + alert.getId() + "]");
        Alert result = em.find(Alert.class, alert.getId());
        return result;
    }

    /**
     * Find filtered range.
     * 
     * @param first
     *            the first
     * @param pageSize
     *            the page size
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<Alert> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        this.currentFilters = filters;
        String qString = buildQuery(true, sortField, sortOrder);
        Query q = em.createQuery(qString);
        if (currentFilters != null && currentFilters.get("nyilvantartasiSzam") != null) {
            q.setParameter("nyszam", (String) currentFilters.get("nyilvantartasiSzam") + "%");
        }
        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        List<Alert> result = q.getResultList();
        return result;
    }

    /**
     * Find All
     * 
     * @param first
     *            the first
     * @param pageSize
     *            the page size
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public List<Alert> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Alert> cq = builder.createQuery(Alert.class);
        Root<Alert> from = cq.from(Alert.class);
        cq.select(from);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if ((currentFilters != null) && !currentFilters.isEmpty()) {
            Set<Entry<String, Object>> es = currentFilters.entrySet();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                String pattern = filter.getValue() + "%";
                x = builder.lower(x);
                pattern = pattern.toLowerCase();
                Predicate predicate = builder.like(x, pattern);
                predicateList.add(predicate);
            }
        }
        if (!predicateList.isEmpty()) {
            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);
        }

        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    private String buildQuery(boolean fetchClient, String sortField, SortOrder sortOrder) {
        String qString = "from Alert a join " + (fetchClient ? "fetch " : "") + "a.client c";

        String typeFilter = null;
        String activeFilter = null;
        String nySzamFilter = null;
        if ((currentFilters != null) && !currentFilters.isEmpty()) {
            for (String key : currentFilters.keySet()) {
                if ("version.type".equals(key)) {
                    boolean isNullAllow = false;
                    String[] visibleClientTypes = (String[]) currentFilters.get(key);
                    if (visibleClientTypes.length > 0) {
                        ArrayList<Integer> ids = new ArrayList<Integer>(visibleClientTypes.length);
                        for (String type : visibleClientTypes) {
                            if ((type != null) && !("".equals(type))) {
                                ids.add(Integer.valueOf(type));
                            } else {
                                isNullAllow = true;
                            }
                        }
                        typeFilter = "";
                        if (ids.size() > 0) {
                            typeFilter = "a.clientType.id in (" + StringUtils.join(ids, ',') + ")";
                        }
                        if (isNullAllow) {

                            if (ids.size() > 0) {
                                typeFilter = "(" + typeFilter + " or a.clientType is null)";
                            } else {
                            typeFilter += "a.clientType is null";
                            }
                        }
                    }
                }
                if ("version.active".equals(key)) {
                    String isActive = (String) currentFilters.get(key);
                    if ("true".equals(isActive) || "false".equals(isActive)) {
                        activeFilter = "a.active is " + isActive;
                    }
                }
                if ("nyilvantartasiSzam".equals(key)) {
                    nySzamFilter = "c.nyilvantartasiSzam like :nyszam";
                }
            }
        }

        if (typeFilter != null || activeFilter != null || nySzamFilter != null) {
            qString += " where ";
        }
        if (typeFilter != null) {
            if (activeFilter != null) {
                typeFilter = "(" + typeFilter + ")";
                qString += typeFilter;
                qString += " and ";
            } else {
                qString += typeFilter;
            }
        }
        if (activeFilter != null) {
            qString += activeFilter;
        }

        if (nySzamFilter != null) {
            if (typeFilter != null || activeFilter != null) {
                qString += " and ";
            }
            qString += nySzamFilter;
        }

        if (sortField != null && sortOrder != null) {
            qString += " order by " + sortField + " " + (sortOrder == SortOrder.DESCENDING ? "desc" : "");
        }
        return qString;
    }

    /**
     * Count filtered file items.
     * 
     * @param filters
     *            the filters
     * @return the int
     */
    public int count(Map<String, Object> actualFilters) {
        this.currentFilters = actualFilters;
        String qString = buildQuery(true, null, null);
        Query q = em.createQuery(qString);
        if (currentFilters != null && currentFilters.get("nyilvantartasiSzam") != null) {
            q.setParameter("nyszam", ((String) currentFilters.get("nyilvantartasiSzam")) + "%");
        }
        currentCount = q.getResultList().size();

        return currentCount;
    }

    public void persistAlert(Alert alert) {
        if (alert.getId() == null) {
            em.persist(alert);
        } else {
            em.merge(alert);
        }
    }
    
    public List<Alert> findAlertWithTitle(Alert alert) {
        Query q = em.createQuery("from Alert where client = :client and title = :title");
        q.setParameter("title", alert.getTitle());
        q.setParameter("client", alert.getClient());
        List<Alert> result = q.getResultList();
        return result;
    }

    public void delete(Alert[] selectedAlerts) {
        if (selectedAlerts != null) {
            for (Alert alert : selectedAlerts) {
                alert = em.find(Alert.class, alert.getId());
                if (alert != null) {
                    em.remove(alert);
                }
            }
        }
    }

    public Alert find(Integer id) {
        Alert alert = em.find(Alert.class, id);
        return alert;
    }

    public List<Alert> getSystemAlerts(Client item) {
        String select = "select a from Alert a join a.client c where c.id = :clientId and a.createdBy is null";
        Query q = em.createQuery(select);
        q.setParameter("clientId", item.getId());
        @SuppressWarnings("unchecked")
        List<Alert> result = (List<Alert>) q.getResultList();
        return result;
    }

    public void remove(Alert alert) {
        alert = em.find(Alert.class, alert.getId());
        em.remove(alert);
    }

    public void deleteAllAlerts() {
        Query q = em.createQuery("delete from Alert");
        q.executeUpdate();
    }

    public ClientChanges mergeClientChanges(ClientChanges version) {        
        return em.merge(version);
    }

    public ClientChanges findClientChanges(ClientChanges version) {
        return em.find(ClientChanges.class, version.getId());
    }

}
