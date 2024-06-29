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

import hu.exprog.beecomposit.back.model.Language;
import hu.exprog.honeyweb.middle.services.BasicService;

@Named
@Stateless
public class LanguageService extends BasicService<Language> implements Serializable {

	private static final long serialVersionUID = -1535993262035500924L;

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	public LanguageService() {

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
	public Language find(Language item) {
		return em.find(Language.class, item.getId());
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	public Class<Language> getDomainClass() {
		return Language.class;
	}

}
