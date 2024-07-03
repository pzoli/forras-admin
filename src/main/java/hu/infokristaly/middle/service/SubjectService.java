/**
 * 
 */
package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.domain.SubjectType;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class SubjectService implements Serializable {

    private static final long serialVersionUID = -2360286862757860424L;

    public static final String LOGGED_IN_SYSTEM_USER = "loggedInUser";

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private Logger log;

    public List<Subject> findAllSubject(Date startDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Subject> cq = builder.createQuery(Subject.class);
        Root<Subject> from = cq.from(Subject.class);
        cq.select(from);
        List<Predicate> predicateList = new ArrayList<Predicate>();

        Expression<String> x = from.get("id");
        Predicate predicate = builder.notEqual(x, -1);
        predicateList.add(predicate);

        if (startDate != null) {
            Expression<Date> d = from.get("deleteDate");
            Predicate predicateDate = builder.greaterThanOrEqualTo(d, startDate);
            Predicate predicateDateIsNull = builder.isNull(d);
            Predicate orPredicates = builder.or(new Predicate[] { predicateDate, predicateDateIsNull });
            predicateList.add(orPredicates);
        }
        
        Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        cq.where(predicates);

        cq.orderBy(builder.asc(from.get("title")));
        Query q = em.createQuery(cq);
        @SuppressWarnings("unchecked")
        List<Subject> result = (List<Subject>) q.getResultList();
        return result;
    }

    public List<SubjectType> findAllSubjectType() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SubjectType> cq = builder.createQuery(SubjectType.class);
        Root<SubjectType> from = cq.from(SubjectType.class);
        cq.select(from);
        cq.orderBy(builder.asc(from.get("name")));
        Query q = em.createQuery(cq);
        @SuppressWarnings("unchecked")
        List<SubjectType> result = (List<SubjectType>) q.getResultList();
        return result;
    }

    public Subject findSubject(Integer primaryKey) {
        return em.find(Subject.class, primaryKey);
    }

    public SubjectType findSubjectType(Integer primaryKey) {
        return em.find(SubjectType.class, primaryKey);
    }

    public void persistSubject(Subject subject) {
        if (subject.getId() == null) {
            em.persist(subject);
        } else {
            em.merge(subject);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Subject> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Subject> cq = builder.createQuery(Subject.class);
        Root<Subject> from = cq.from(Subject.class);
        cq.select(from);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if ((filters != null) && !filters.isEmpty()) {
            Set<Entry<String, Object>> es = filters.entrySet();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                Predicate predicate;
                if (filter.getKey().equals("deleteDate")) {
                    if (Boolean.TRUE.equals(filter.getValue())) {
                        predicate = builder.isNotNull(x);
                    } else {
                        predicate = builder.isNull(x);
                    }
                } else {
                    String pattern = filter.getValue() + "%";
                    x = builder.lower(x);
                    pattern = pattern.toLowerCase();
                    predicate = builder.like(x, pattern);
                }
                predicateList.add(predicate);
            }
        }
        Expression<String> x = from.get("id");
        Predicate predicate = builder.notEqual(x, -1);
        predicateList.add(predicate);

        Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        cq.where(predicates);

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
        Root<Subject> from = cq.from(Subject.class);
        cq.select(builder.count(from));

        List<Predicate> predicateList = new ArrayList<Predicate>();
        if ((actualFilters != null) && !actualFilters.isEmpty()) {
            Set<Entry<String, Object>> es = actualFilters.entrySet();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                Predicate predicate;
                if (filter.getKey().equals("deleteDate")) {
                    if (Boolean.TRUE.equals(filter.getValue())) {
                        predicate = builder.isNotNull(x);
                    } else {
                        predicate = builder.isNull(x);
                    }
                } else {
                    String pattern = filter.getValue() + "%";
                    x = builder.lower(x);
                    pattern = pattern.toLowerCase();
                    predicate = builder.like(x, pattern);
                }
                predicateList.add(predicate);
            }
        }
        Expression<String> x = from.get("id");
        Predicate predicate = builder.notEqual(x, -1);
        predicateList.add(predicate);

        Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        cq.where(predicates);

        Query q = em.createQuery(cq);
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }

    public void deleteSubject(Subject item) {
        item = findSubject(item.getId());
        em.remove(item);
    }

    public void merge(Subject item) {
        em.merge(item);
    }

}
