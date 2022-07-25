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
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import hu.infokristaly.back.domain.Doctor;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class DoctorService implements Serializable {

    private static final long serialVersionUID = 1030025159894302867L;

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    public List<Doctor> findAllDoctor() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Doctor> cq = builder.createQuery(Doctor.class);
        Root<Doctor> from = cq.from(Doctor.class);
        cq.select(from);
        cq.orderBy(builder.asc(from.get("name")));
        Query q = em.createQuery(cq);
        @SuppressWarnings("unchecked")
        List<Doctor> result = (List<Doctor>) q.getResultList();
        return result;
    }

    public Doctor find(Integer primaryKey) {
        return em.find(Doctor.class, primaryKey);
    }

    public Doctor find(Doctor doc) {
        return em.find(Doctor.class, doc.getId());
    }

    public void persist(Doctor doctor) {
        if (doctor.getId() == null) {
            em.persist(doctor);
        } else {
            em.merge(doctor);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Doctor> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Doctor> cq = builder.createQuery(Doctor.class);
        Root<Doctor> from = cq.from(Doctor.class);
        cq.select(from);

        if ((filters != null) && !filters.isEmpty()) {
            Set<Entry<String, Object>> es = filters.entrySet();
            List<Predicate> predicateList = new ArrayList<Predicate>();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                String pattern = filter.getValue() + "%";
                x = builder.lower(x);
                pattern = pattern.toLowerCase();
                Predicate predicate = builder.like(x, pattern);
                predicateList.add(predicate);
            }

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
        Root<Doctor> from = cq.from(Doctor.class);
        cq.select(builder.count(from));

        if ((actualFilters != null) && !actualFilters.isEmpty()) {
            Set<Entry<String, Object>> es = actualFilters.entrySet();
            List<Predicate> predicateList = new ArrayList<Predicate>();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                String pattern = filter.getValue() + "%";
                x = builder.lower(x);
                pattern = pattern.toLowerCase();
                Predicate predicate = builder.like(x, pattern);
                predicateList.add(predicate);
            }

            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);

        }

        Query q = em.createQuery(cq);
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }

    public void delete(Doctor item) {
        item = find(item.getId());
        em.remove(item);
    }

}
