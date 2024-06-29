package hu.exprog.beecomposit.middle.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.exprog.beecomposit.back.model.UserRegister;
import hu.exprog.beecomposit.back.resources.AuthBackingBean;
import hu.exprog.honeyweb.middle.services.BasicService;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class UserRegisterService extends BasicService<UserRegister> implements Serializable {

	private static final long serialVersionUID = -2360286862757860424L;

	public static final String LOGGED_IN_SYSTEM_USER = "loggedInUser";

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	public static String USER_GROUP = "ROLE_USER";
	public static String ADMIN_GROUP = "ROLE_ADMIN";

	@Override
	protected Query buildCountQuery(Map<String, Object> filters) {
		String result = buildQueryString(filters);
		result += "select count(*) " + result;
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
		return queryBuff.toString();
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
	public Logger getLogger() {
		return log;
	}

	@Override
	public UserRegister find(UserRegister item) {
		UserRegister result = (UserRegister) getEm().find(item.getClass(), item.getId());
		if (result != null) {
			getLogger().info("Find by Id [" + item.getId() + "]" + result);
		} else {
			getLogger().severe("UserRegister not found with [id:" + item.getId() + "]");
		}
		return result;
	}

	@Override
	public Class<UserRegister> getDomainClass() {
		return UserRegister.class;
	}

	public List<UserRegister> getLastIncompleteUserRegister() {
		Query q = em.createQuery("from UserRegister where mailSentDate is null and mailSendTryCounter < 6 order by registeredDate, mailSendTryCounter");
		q.setMaxResults(10);
		List<UserRegister> result = q.getResultList();
		return result;
	}

	public void updateMailSendTryCount(UserRegister userRegister) {
		userRegister.setMailSendTryCounter((byte) (userRegister.getMailSendTryCounter() + 1));
		em.merge(userRegister);
	}

	public UserRegister findByHashId(String hashId) {
		Query q = em.createQuery("from UserRegister where hashId = :hashId and activationDate is null");
		q.setParameter("hashId", hashId);
		List<UserRegister> result = q.getResultList();
		return result.size() > 0 ? result.get(0) : null;
	}
}
