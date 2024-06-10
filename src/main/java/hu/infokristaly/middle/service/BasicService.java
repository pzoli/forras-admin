package hu.infokristaly.middle.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.model.SortOrder;

public abstract class BasicService<T> {

	public void persist(T item) {
		getEm().persist(item);
	}

	public T merge(T item) {
        return getEm().merge(item);
	}
	
	public void remove(T item) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Long id = Long.valueOf(BeanUtils.getProperty(item, "id"));
		item = getEm().find(getDomainClass(),id);
		getEm().remove(item);
	}
	
	public T find(Class<T> type, Long primaryKey) {		
		T result = (T) getEm().find(type, primaryKey);
		if (result != null) {
			getLogger().info("Find by Id [" + primaryKey + "]" + result);
		} else {
			getLogger().severe("Object not found with [id:" + primaryKey + "]");
		}
		return result;
	}
	
	protected abstract String buildQueryString(Map<String, Object> filters);
	protected abstract Query buildCountQuery(Map<String, Object> filters);
	protected abstract Query buildQuery(String sortField, SortOrder sortOrder, Map<String, Object> filters);
	protected abstract void setQueryParams(Query q, Map<String, Object> filters);
	
	@SuppressWarnings("unchecked")
	public List<T> findRangeByQString(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<T> result = null;
		Query q = buildQuery(sortField, sortOrder, filters);
		setQueryParams(q, filters);
		q.setFirstResult(first);
		q.setMaxResults(pageSize);
		result = q.getResultList();
		return result;
	}

	public List<T> findAll() {
		List<T> result = null;
		Query q = getEm().createQuery("from " + getDomainClass().getName());
		result = q.getResultList();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findRange(int first, int pageSize, String sortField, hu.infokristaly.middle.service.SortOrder sortOrder, Map<String, Object> filters) {
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery<T> cq = builder.createQuery(getDomainClass());
		Root<T> from = cq.from(getDomainClass());
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
			if (sortOrder.name().equals(SortOrder.ASCENDING.name())) {
				cq.orderBy(builder.asc(from.get(sortField)));
			} else if (sortOrder.name().equals(SortOrder.DESCENDING.name())) {
				cq.orderBy(builder.desc(from.get(sortField)));
			}
		}
		Query q = getEm().createQuery(cq);
		q.setMaxResults(pageSize);
		q.setFirstResult(first);
		return q.getResultList();
	}

	public int count(Map<String, Object> actualFilters) {
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery cq = builder.createQuery();
		Root<T> from = cq.from(getDomainClass());
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

		Query q = getEm().createQuery(cq);
		int result = ((Long) q.getSingleResult()).intValue();
		return result;
	}
	
	public int countByQString(Map<String, Object> actualFilters) {
		int result = 0;
		Query q = buildCountQuery(actualFilters);
		result = ((Long)q.getSingleResult()).intValue();
		return result;
	}

	public abstract EntityManager getEm();
	public abstract Logger getLogger();
	public abstract T find(T item);
	public abstract Class<T> getDomainClass();
}
