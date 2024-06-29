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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import hu.infokristaly.back.domain.ClientType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.io.Serializable;

/**
 * The FileInfoService implements Serializable, Serializable class. Used for
 * manage fileInfo entities.
 * 
 * @author pzoli
 * @param <FilterOptions>
 * @param <Restrictor>
 * 
 */

@Stateless
@Named
public class ClientTypeService implements Serializable {

    private static final long serialVersionUID = 4896024140026461148L;

    /** The logger. */
    @Inject
    private Logger log;

    /** The entity manager. */
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    public ClientTypeService() {

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
    public ClientType find(ClientType client) {
        ClientType result = em.find(ClientType.class, client.getId());
        if (result != null) {
            log.info("Found [" + client.getId() + "] = " + result.getTypename());
        } else {
            log.info("Found [" + client.getId() + "] = null");
        }

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
    public List<ClientType> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ClientType> cq = builder.createQuery(ClientType.class);
        Root<ClientType> from = cq.from(ClientType.class);
        cq.select(from);

        cq.orderBy(builder.asc(from.get("typename")));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    public void mergeClientType(ClientType clientType) {
        em.merge(clientType);
    }

    public void persistClientType(ClientType clientType) {
        em.persist(clientType);
    }

    public ClientType findClientType(ClientType clientType) {
        ClientType result = em.find(ClientType.class, clientType.getId());
        return result;
    }

    public ClientType findClientType(Integer primaryKey) {
        ClientType result = em.find(ClientType.class, primaryKey);
        return result;
    }

    public List<ClientType> findRange(int first, int pageSize, String actualOrderField, SortOrder actualSortOrder, Map<String, Object> filters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ClientType> cq = builder.createQuery(ClientType.class);
        Root<ClientType> from = cq.from(ClientType.class);
        cq.select(from);

        if ((actualOrderField != null) && !actualOrderField.isEmpty()) {
            if (actualSortOrder.equals(SortOrder.ASCENDING)) {
                cq.orderBy(builder.asc(from.get(actualOrderField)));
            } else {
                cq.orderBy(builder.desc(from.get(actualOrderField)));
            }
        }

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if ((filters != null) && !filters.isEmpty()) {
            Set<Entry<String, Object>> es = filters.entrySet();
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
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        return q.getResultList();
    }

    public int count(Map<String, Object> actualfilters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ClientType> cq = builder.createQuery(ClientType.class);
        Root<ClientType> from = cq.from(ClientType.class);
        cq.select(from);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if ((actualfilters != null) && !actualfilters.isEmpty()) {
            Set<Entry<String, Object>> es = actualfilters.entrySet();
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
        return q.getResultList().size();
    }

    public void deleteClientType(ClientType item) {
        item = em.find(ClientType.class, item.getId());
        em.remove(item);
    }

}
