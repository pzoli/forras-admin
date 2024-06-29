package hu.exprog.beecomposit.middle.service;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.SortOrder;

import hu.exprog.beecomposit.back.model.SystemUser;
import hu.exprog.beecomposit.back.model.UserJoinGroup;
import hu.exprog.beecomposit.back.model.UserRegister;
import hu.exprog.beecomposit.back.model.Usergroup;
import hu.exprog.beecomposit.back.resources.AuthBackingBean;
import hu.exprog.honeyweb.middle.services.BasicService;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class SystemUserService extends BasicService<SystemUser> implements Serializable {

	private static final long serialVersionUID = -2360286862757860424L;

	public static final String LOGGED_IN_SYSTEM_USER = "loggedInUser";

	@Inject
	private AuthBackingBean auth;

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private Logger log;

	@Inject
	private UserGroupService userGroupService;

	public static String USER_GROUP = "ROLE_USER";
	public static String ADMIN_GROUP = "ROLE_ADMIN";

	public void createRole(UserJoinGroup userJoinGroup) {
		em.persist(userJoinGroup);
	}

	public SystemUser getLoggedInSystemUser(Principal principal) {
		SystemUser result = null;
		if (principal != null) {
			Query q = em.createQuery("from SystemUser u where u.osUserName = :emailAddress and u.enabled = true");

			q.setParameter("emailAddress", principal.getName());
			result = (SystemUser) q.getSingleResult();
		}
		return result;

	}

	public void persistSystemUser(SystemUser newSystemUser) {
		if (newSystemUser.getId() == null) {
			auth.create(newSystemUser);
		} else {
			if (StringUtils.isEmpty(newSystemUser.getOsUserPassword())) {
				SystemUser systemUser = find(newSystemUser);
				String password = systemUser.getOsUserPassword();
				newSystemUser.setOsUserPassword(password);
			} else {
				String userPassword = auth.getUserPassword(newSystemUser);
				newSystemUser.setOsUserPassword(userPassword);
			}

			newSystemUser = em.merge(newSystemUser);

		}

	}

	/**
	 * Find All
	 * 
	 * @param first
	 *            the first
	 * @param pageSize
	 *            the page size
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<SystemUser> findAll(boolean active) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<SystemUser> cq = builder.createQuery(SystemUser.class);
		Root<SystemUser> from = cq.from(SystemUser.class);
		cq.select(from);
		if (active) {
			List<Predicate> predicateList = new ArrayList<Predicate>();

			Expression<String> x = from.get("enabled");
			Predicate predicate = builder.equal(x, true);
			predicateList.add(predicate);

			Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
			cq.where(predicates);
		}
		cq.orderBy(builder.asc(from.get("userName")));
		Query q = em.createQuery(cq);
		return q.getResultList();
	}

	public SystemUser getLoggedInSystemUser() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession session = request.getSession(false);
		SystemUser user = (SystemUser) session.getAttribute(LOGGED_IN_SYSTEM_USER);
		if (user == null) {
			Principal principal = auth.getLoggedInPrincipal();
			if (principal != null) {
				user = getLoggedInSystemUser(principal);
				UserJoinGroup group = findUserJoinGroup(user.getOsUserName());
				if (group != null) {
					session.setAttribute(LOGGED_IN_SYSTEM_USER, user);
				}
			}
		}
		return user;
	}

	public void removeSystemUser(SystemUser item) {
		item = find(item);
		em.remove(item);
	}

	public boolean isAdmin() {
		SystemUser user = getLoggedInSystemUser();
		boolean result = (user != null) && (getRole(user).equals(ADMIN_GROUP));
		return result;
	}

	public String getRole(SystemUser user) {
		if (user != null) {
			UserJoinGroup group = findUserJoinGroup(user.getOsUserName());
			if (group != null) {
				return group.getUserJoinGroupId().getGroupName();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public UserJoinGroup findUserJoinGroup(String emailAddress) {
		Query q = em.createQuery("from UserJoinGroup where userJoinGroupId.user_name = : userName");
		List<UserJoinGroup> results = q.getResultList();
		return results.size() > 0 ? results.get(0) : null;
	}

	public void removeUserJoinGroup(UserJoinGroup userJoinGroup) {
		userJoinGroup = em.find(UserJoinGroup.class, userJoinGroup.getUserJoinGroupId());
		em.remove(userJoinGroup);
	}

	public void mergeUserJoinGroup(UserJoinGroup userJoinGroup) {
		em.merge(userJoinGroup);
	}

	public void persistUserJoinGroup(UserJoinGroup userJoinGroup) {
		em.persist(userJoinGroup);
	}

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
	public SystemUser find(SystemUser item) {
		SystemUser result = (SystemUser) getEm().find(item.getClass(), item.getId());
		if (result != null) {
			getLogger().info("Find by Id [" + item.getId() + "]" + result);
		} else {
			getLogger().severe("SystemUser not found with [id:" + item.getId() + "]");
		}
		return result;
	}

	@Override
	public Class<SystemUser> getDomainClass() {
		return SystemUser.class;
	}

	public void removeRoles(SystemUser user) {
		Query q = em.createQuery("delete from UserJoinGroup u where u.userJoinGroupId.userName = :userName");
		q.setParameter("userName", user.getOsUserName());
		q.executeUpdate();
	}

	public SystemUser findByOsUserName(String loggedInPrincipal) {
		Query q = em.createQuery("from SystemUser u where u.osUserName = :emailAddress and u.enabled = true");
		q.setParameter("emailAddress", loggedInPrincipal);
		List<SystemUser> users = q.getResultList();
		return users.size() > 0 ? users.get(0) : null;
	}

	public SystemUser createUserFromRegistration(UserRegister userRegister) {
		SystemUser result = null;
		if (userRegister != null) {
			result = new SystemUser();
			result.setUserName(userRegister.getUserName());
			result.setOsUserName(userRegister.getEmailAddress());
			result.setSqlserverloginname(userRegister.getEmailAddress());
			Usergroup usergroup = userGroupService.findUserRoleGroup("ROLE_USER");
			result.setUsergroup(usergroup);
			result.setOsUserPassword(userRegister.getUserPassword());
			auth.create(result);
		}
		return result;
	}

}
