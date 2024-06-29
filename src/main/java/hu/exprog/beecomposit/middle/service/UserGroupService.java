package hu.exprog.beecomposit.middle.service;

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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.exprog.beecomposit.back.model.Usergroup;
import hu.exprog.honeyweb.middle.services.BasicService;

@Named
@Stateless
public class UserGroupService extends BasicService<Usergroup> implements Serializable {

	private static final long serialVersionUID = 4814838304056343071L;

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	public UserGroupService() {

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
	public Usergroup find(Usergroup item) {
		return em.find(Usergroup.class, item.getId());
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Class<Usergroup> getDomainClass() {
		return Usergroup.class;
	}

	@SuppressWarnings("unchecked")
	public List<Usergroup> findAll() {
		Query q = em.createQuery("from Usergroup");
		return q.getResultList();
	}

	public Usergroup findUserRoleGroup(String rolegroup) {
		Query q = em.createQuery("from Usergroup where rolegroup = :rolegroup");
		q.setParameter("rolegroup", rolegroup);
		List<Usergroup> result = q.getResultList();
		return result.size() > 0 ? result.get(0) : null;
	}

}
