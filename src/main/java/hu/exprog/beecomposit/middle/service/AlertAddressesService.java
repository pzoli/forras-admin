package hu.exprog.beecomposit.middle.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.exprog.beecomposit.back.model.AlertAddresses;
import hu.exprog.honeyweb.middle.services.BasicService;

@Stateless
public class AlertAddressesService extends BasicService<AlertAddresses> {

	@PersistenceContext(unitName = "primary")
	private EntityManager emConfig;

	@Inject
	private Logger logger;

	@Override
	protected String buildQueryString(Map<String, Object> filters) {
		StringBuffer queryBuff = new StringBuffer();
		queryBuff.append("from " + getDomainClass().getSimpleName());
		return queryBuff.toString();
	}

	@Override
	protected Query buildCountQuery(Map<String, Object> filters) {
		String result = buildQueryString(filters);
		result += "select count(*) " + result;
		Query q = emConfig.createQuery(result);
		return q;
	}

	@Override
	protected Query buildQuery(String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		String result = buildQueryString(filters);
		Query q = emConfig.createQuery(result);
		return q;
	}

	@Override
	protected void setQueryParams(Query q, Map<String, Object> filters) {
		filters.entrySet().stream().forEach((Entry<String, Object> e) -> {
			q.setParameter(e.getKey(), e.getValue());	
		});		
	}

	@Override
	public EntityManager getEm() {
		return emConfig;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public AlertAddresses find(AlertAddresses item) {
		return emConfig.find(AlertAddresses.class, item.getId());
	}

	@Override
	public Class<AlertAddresses> getDomainClass() {
		return AlertAddresses.class;
	}
	
}
