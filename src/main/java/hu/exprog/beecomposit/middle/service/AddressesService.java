package hu.exprog.beecomposit.middle.service;

import java.io.Serializable;
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

import org.primefaces.model.SortOrder;

import hu.exprog.beecomposit.back.model.Addresses;
import hu.exprog.honeyweb.middle.services.BasicService;

@Named
@Stateless
public class AddressesService extends BasicService<Addresses> implements Serializable {

	private static final long serialVersionUID = -9139027636913174016L;

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	public AddressesService() {

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
		queryBuff.append("from " + getDomainClass().getSimpleName());
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
	public Addresses find(Addresses item) {
		return em.find(Addresses.class, item.getId());
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Class<Addresses> getDomainClass() {
		return Addresses.class;
	}

}
