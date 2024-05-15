package hu.infokristaly.middle.service;

import java.io.Serializable;
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
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.infokristaly.back.domain.RFIDCard;
import hu.infokristaly.back.domain.RFIDCardUser;

@Named
@Stateless
public class RFIDCardUserService extends BasicService<RFIDCardUser> implements
		Serializable {

	private static final long serialVersionUID = -9139027636913174016L;

	@Inject
	private EntityManager em;
	
	@Inject
	private Logger log;

	public RFIDCardUserService() {

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
		queryBuff.append("from RFIDCardUser");
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
	public RFIDCardUser find(RFIDCardUser item) {
		return em.find(RFIDCardUser.class, item.getId());
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Class<RFIDCardUser> getDomainClass() {
		return RFIDCardUser.class;
	}

    public RFIDCardUser findActiveByRfidCard(RFIDCard rFIDCard, Date date) {
        Query q = em.createQuery("from RFIDCardUser where (rfidCard.id = :rfidCardID) and (periodStart <= :dateoflog) and ((periodEnd is null) or (periodEnd >= :dateoflog))");
        q.setParameter("rfidCardID", rFIDCard.getId());
        q.setParameter("dateoflog", date);
        List<RFIDCardUser> result = q.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
