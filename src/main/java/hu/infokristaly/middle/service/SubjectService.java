package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
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
import hu.exprog.honeyweb.middle.services.BasicService;
import hu.infokristaly.back.domain.Subject;
import hu.infokristaly.back.domain.SubjectType;

@Named
@Stateless
public class SubjectService extends BasicService <Subject> implements Serializable {

    private static final long serialVersionUID = -9139027636913174016L;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private Logger log;

    public SubjectService() {

    }

    @PostConstruct
    public void init() {
    }

    @Override
    protected Query buildCountQuery(Map<String, Object> filters) {
        String result = buildQueryString(filters);
        result = "select count(*) " + result;
        Query q = em.createQuery(result);
        return q;
    }

    @Override
    protected Query buildQuery(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        String result = buildQueryString(filters);
        Query q = em.createQuery(result);
        return q;
    }

    @Override
    protected String buildQueryString(Map<String, Object> filters) {
        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("from Doctor");
        filters.keySet().stream().forEach(key -> {
            Object o = filters.get(key);
            Map<String, String> desc = getDescriptor(key);
            queryBuff.append(" ");
        });
        return queryBuff.toString();
    }

    private Map<String, String> getDescriptor(String key) {
        return null;
    }

    public SubjectType findSubjectType(Long primaryKey) {
        return em.find(SubjectType.class, primaryKey);
    }

    public List<SubjectType> findAllSubjectType() {
        return em.createQuery("from SubjectType").getResultList();
    }
    
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

    @Override
    protected void setQueryParams(Query q, Map<String, Object> filters) {
        filters.entrySet().stream().forEach((Entry<String, Object> e) -> {
            q.setParameter(e.getKey(), e.getValue());
        });
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Subject find(Subject item) {
        return em.find(Subject.class, item.getId());
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Class<Subject> getDomainClass() {
        return Subject.class;
    }

}
