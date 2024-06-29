/**
 * 
 */
package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

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

import hu.infokristaly.back.domain.EventTemplate;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class EventTemplateService implements Serializable {

    private static final long serialVersionUID = -2360286862757860424L;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserService userService;

    @Inject
    private Logger log;

    public List<EventTemplate> findAllTemplate() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EventTemplate> cq = builder.createQuery(EventTemplate.class);
        Root<EventTemplate> from = cq.from(EventTemplate.class);
        cq.select(from);

        cq.orderBy(builder.asc(from.get("title")));
        Query q = em.createQuery(cq);
        @SuppressWarnings("unchecked")
        List<EventTemplate> result = (List<EventTemplate>) q.getResultList();
        return result;
    }

    public EventTemplate findTemplate(Long primaryKey) {
        return em.find(EventTemplate.class, primaryKey);
    }

    public void persistTemplate(EventTemplate eventTemplate) {
        if (eventTemplate.getId() == null) {
            em.persist(eventTemplate);
        } else {
            em.merge(eventTemplate);
        }
    }

    @SuppressWarnings("unchecked")
    public List<EventTemplate> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<EventTemplate> cq = builder.createQuery(EventTemplate.class);
        Root<EventTemplate> from = cq.from(EventTemplate.class);
        cq.select(from);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (filters != null && !filters.isEmpty()) {
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
        return q.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int count(Map<String, Object> actualFilters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery cq = builder.createQuery();
        Root<EventTemplate> from = cq.from(EventTemplate.class);
        cq.select(builder.count(from));

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (actualFilters != null && !actualFilters.isEmpty()) {
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

    public void deleteTemplate(EventTemplate item) {
        item = findTemplate(item.getId());
        em.remove(item);
    }

}
