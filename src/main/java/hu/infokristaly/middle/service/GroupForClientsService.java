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

import hu.infokristaly.back.domain.Client;
import hu.infokristaly.back.domain.GroupForClients;
import hu.infokristaly.back.domain.SubjectType;
import hu.exprog.beecomposit.back.model.SystemUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.io.Serializable;

@Stateless
@Named
public class GroupForClientsService implements Serializable {

    private static final long serialVersionUID = 4296408007529101008L;

    /** The logger. */
    @Inject
    private Logger log;

    /** The entity manager. */
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserService userService;

    private Map<String, Object> currentFilters;

    public GroupForClientsService() {

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
    public GroupForClients find(GroupForClients group) {
        log.info("Find by Id [" + group.getId() + "]" + group.getName());
        GroupForClients result = em.find(GroupForClients.class, group.getId());
        if (result != null) {
            for (Client client : result.getClients()) {
                client.getId();
            }
        }
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
    public List<GroupForClients> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        currentFilters = filters;

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<GroupForClients> cq = builder.createQuery(GroupForClients.class);
        Root<GroupForClients> from = cq.from(GroupForClients.class);
        cq.select(from);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Expression<Long> idField = from.get("id");
        Long id = -1L;
        Predicate idpredicate = builder.greaterThan(idField, id);
        predicateList.add(idpredicate);
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

        if (!userService.getLoggedInSystemUser().isAdminUser()) {
            Expression<String> created_by = from.get("createdBy");
            Predicate userPredicate = builder.equal(created_by, userService.getLoggedInSystemUser().getId());
            predicateList.add(userPredicate);
        }

        if (!predicateList.isEmpty()) {
            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);
        }

        if (sortField != null) {
            if (sortOrder.equals(SortOrder.ASCENDING)) {
                cq.orderBy(builder.asc(from.get(sortField)));
            } else if (sortOrder.equals(SortOrder.DESCENDING)) {
                cq.orderBy(builder.desc(from.get(sortField)));
            }
        }

        Query q = em.createQuery(cq);
        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        List<GroupForClients> result = q.getResultList();
        for (GroupForClients group : result) {
            List<Client> clients = group.getClients();
            for (Client client : clients) {
                client.getAccessibles();
            }
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
    public List<GroupForClients> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<GroupForClients> cq = builder.createQuery(GroupForClients.class);
        Root<GroupForClients> from = cq.from(GroupForClients.class);
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
        if (!userService.getLoggedInSystemUser().isAdminUser()) {
            Expression<String> created_by = from.get("createdBy");
            Predicate userNull = builder.isNull(created_by);                       
            Predicate userPredicate = builder.equal(created_by, userService.getLoggedInSystemUser().getId());
            userPredicate = builder.or(userPredicate,userNull);
            predicateList.add(userPredicate);
        }
        if (!predicateList.isEmpty()) {
            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));            
            cq.where(predicates);
        }
        cq.orderBy(builder.asc(from.get("name")));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    /**
     * Count filtered file items.
     * 
     * @param filters
     *            the filters
     * @return the int
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int count(Map<String, Object> actualFilters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery cq = builder.createQuery();
        Root<GroupForClients> from = cq.from(GroupForClients.class);
        cq.select(builder.count(from));

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Expression<Long> idField = from.get("id");
        Long id = -1L;
        Predicate idpredicate = builder.greaterThan(idField, id);
        predicateList.add(idpredicate);
        if ((actualFilters != null) && !actualFilters.isEmpty()) {
            Set<Entry<String, Object>> es = actualFilters.entrySet();
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

        if (!userService.getLoggedInSystemUser().isAdminUser()) {
            Expression<String> created_by = from.get("createdBy");
            Predicate userPredicate = builder.equal(created_by, userService.getLoggedInSystemUser().getId());
            predicateList.add(userPredicate);
        }

        if (!predicateList.isEmpty()) {
            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);
        }

        Query q = em.createQuery(cq);
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }

    public void persistGroup(GroupForClients newGroup) {
        if (newGroup.getId() == null) {
            try {
                newGroup.setCreatedBy(userService.getLoggedInSystemUser());
                em.persist(newGroup);
            } catch (Exception ex) {
                System.out.print(ex);
            }
        } else {
            em.merge(newGroup);
        }
    }

    public void delete(GroupForClients[] selectedGroups) {
        for (GroupForClients group : selectedGroups) {
            if (!group.getId().equals(-1)) {
                group = em.find(GroupForClients.class, group.getId());
                if (group != null) {
                    em.remove(group);
                }
            }
        }
    }

    public GroupForClients find(Integer id) {
        GroupForClients group = em.find(GroupForClients.class, id);
        return group;
    }

    public void removeClient(Client item) {
        List<GroupForClients> groups = findAll();
        for (GroupForClients group : groups) {
            if (group.getClients().contains(item)) {
                group.getClients().remove(item);
                em.persist(group);
            }
        }
    }

}
