package hu.exprog.honeyweb.middle.services;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
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
		item = getEm().find(getDomainClass(), id);
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
	public List<T> findRange(int first, int pageSize, String sortField, hu.exprog.honeyweb.middle.services.SortOrder sortOrder, Map<String, Object> filters) {
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery<T> cq = builder.createQuery(getDomainClass());
		Root<T> from = cq.from(getDomainClass());

		if ((filters != null) && !filters.isEmpty()) {
			Set<Entry<String, Object>> es = filters.entrySet();
			List<Predicate> predicateList = preparePredicates(filters, from, builder);
			cq.select(from);
			Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
			cq.where(predicates);

		}
		if (sortField != null) {
			Expression<?> x = null;
			if (sortField.contains(".")) {
				String fieldName = sortField.substring(0, sortField.indexOf("."));
				String propertyName = sortField.substring(sortField.indexOf(".") + 1);
				Optional<Join<T, ?>> joinForAlias = from.getJoins().stream().filter(j -> j.getAlias().equals(fieldName + "_" + propertyName)).findFirst();
				if (joinForAlias.isPresent()) {
					x = joinForAlias.get().get(propertyName);
				} else {
					Join<?, ?> join = from.join(fieldName);
					join.alias(fieldName + "_" + propertyName);
					x = join.get(propertyName);					
				}
			} else {
				x = from.get(sortField);
			}

			if (x != null) {
				if (sortOrder.equals(hu.exprog.honeyweb.middle.services.SortOrder.ASCENDING)) {
					cq.orderBy(builder.asc(x));
				} else if (sortOrder.equals(hu.exprog.honeyweb.middle.services.SortOrder.DESCENDING)) {
					cq.orderBy(builder.desc(x));
				}
			}
		}
		Query q = getEm().createQuery(cq);
		q.setMaxResults(pageSize);
		q.setFirstResult(first);
		return q.getResultList();
	}

	private List<Predicate> preparePredicates(Map<String, Object> filters, Root<T> from, CriteriaBuilder builder) {
		List<Predicate> predicateList = new ArrayList<Predicate>();
		SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
		for (Entry<String, Object> filter : filters.entrySet()) {
			String field = filter.getKey();
			Expression<?> x = null;
			if (field.contains(".")) {
				String fieldName = field.substring(0, field.indexOf("."));
				String propertyName = field.substring(field.indexOf(".") + 1);

				Join<?, ?> join = from.join(fieldName);
				join.alias(fieldName + "_" + propertyName);
				field = propertyName;
				x = join.get(propertyName);
			} else {
				x = from.get(field);
			}

			if (x.getJavaType().equals(String.class)) {
				x = builder.lower((Expression<String>) x);
			}

			if (x != null) {
				Predicate predicate = null;
				if (x.getJavaType().equals(String.class)) {
					String pattern = filter.getValue() + "%";
					pattern = pattern.toLowerCase();
					predicate = builder.like((Expression<String>) x, pattern);
				} else if (x.getJavaType().equals(Boolean.class)) {
					if (((String) filter.getValue()).toLowerCase().startsWith("t")) {
						predicate = builder.isTrue((Expression<Boolean>) x);
					} else {
						predicate = builder.isFalse((Expression<Boolean>) x);
					}
				} else if (x.getJavaType().equals(Integer.class)) {
					predicate = builder.ge((Expression<Integer>) x, Integer.valueOf((String) filter.getValue()));
				} else if (x.getJavaType().equals(Date.class)) {
					try {
						Date parsedDate = dateParser.parse((String) filter.getValue());
						predicate = builder.equal((Expression<Date>) x, parsedDate);
					} catch (Exception e) {
					}
				} else {
					predicate = builder.equal((Expression<Object>) x, filter.getValue());
				}
				if (predicate != null) {
					predicateList.add(predicate);
				}
			}
		}
		return predicateList;
	}

	public int count(Map<String, Object> actualFilters) {
		CriteriaBuilder builder = getEm().getCriteriaBuilder();
		CriteriaQuery cq = builder.createQuery();
		Root<T> from = cq.from(getDomainClass());
		cq.select(builder.count(from));

		if ((actualFilters != null) && !actualFilters.isEmpty()) {
			Set<Entry<String, Object>> es = actualFilters.entrySet();
			List<Predicate> predicateList = preparePredicates(actualFilters, from, builder);
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
		result = ((Long) q.getSingleResult()).intValue();
		return result;
	}

	public abstract EntityManager getEm();

	public abstract Logger getLogger();

	public abstract T find(T item);

	public abstract Class<T> getDomainClass();
}
