package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.infokristaly.back.domain.RFIDCard;
import hu.infokristaly.back.domain.RFIDCardReader;

@Named
@Stateless
public class RFIDCardReadersService extends BasicService<RFIDCardReader> implements
		Serializable {

	private static final long serialVersionUID = -9139027636913174016L;

	@Inject
	private EntityManager em;
	
	@Inject
	private Logger log;

	public RFIDCardReadersService() {

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
	protected Query buildQuery(String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		String result = buildQueryString(filters);
		Query q = em.createQuery(result);
		return q;
	}

	@Override
	protected String buildQueryString(Map<String, Object> filters) {
		StringBuffer queryBuff = new StringBuffer();
		queryBuff.append("from RFIDCardReader");
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
		filters.entrySet().stream().forEach((Entry<String, Object>e) -> {
			q.setParameter(e.getKey(), e.getValue());	
		});
	}

	@Override
	public EntityManager getEm() {
		return em;
	}

	@Override
	public RFIDCardReader find(RFIDCardReader item) {
		return em.find(RFIDCardReader.class, item.getId());
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Class<RFIDCardReader> getDomainClass() {
		return RFIDCardReader.class;
	}

    public RFIDCardReader findByReaderId(RFIDCardReader rfidCardReader) {
        Query q = em.createQuery("from RFIDCardReader where readerId = :readerId");
        q.setParameter("readerId", rfidCardReader.getReaderId());
        List<RFIDCardReader> result = q.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
