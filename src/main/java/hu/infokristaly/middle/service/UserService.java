/**
 * 
 */
package hu.infokristaly.middle.service;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.model.SortOrder;

import com.google.zxing.WriterException;

import hu.infokristaly.back.auth.AuthBackingBean;
import hu.infokristaly.back.model.SystemUser;
import hu.infokristaly.back.model.UserJoinGroup;
import hu.infokristaly.back.model.UserJoinGroupId;
import hu.infokristaly.back.model.UserSettings;
import hu.infokristaly.back.resources.QRCodeGenerator;

/**
 * @author pzoli
 * 
 */
@Named
@Stateless
public class UserService implements Serializable {

    private static final long serialVersionUID = -2360286862757860424L;

    public static final String LOGGED_IN_SYSTEM_USER = "loggedInUser";

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    @Inject
    private AuthBackingBean auth;

    public static String USER_GROUP = "ROLE_USER";
    public static String ADMIN_GROUP = "ROLE_ADMIN";

    public void createUser(SystemUser user) {
        em.persist(user);
    }

    public void createRole(UserJoinGroup userJoinGroup) {
        em.persist(userJoinGroup);
    }

    public SystemUser getLoggedInSystemUser(Principal principal) {
        SystemUser result = null;
        if (principal != null) {
            Query q = em.createQuery("Select u from SystemUser u where u.emailAddress = :emailAddress and u.enabled = true");

            q.setParameter("emailAddress", principal.getName());
            result = (SystemUser) q.getSingleResult();
        }
        return result;

    }

    public void persistSystemUser(SystemUser newSystemUser) {
        if (newSystemUser.getUserid() == null) {
            auth.create(newSystemUser);
        } else {
            if ((newSystemUser.getUserpassword() == null) || (newSystemUser.getUserpassword().isEmpty())) {
                SystemUser systemUser = find(newSystemUser);
                String password = systemUser.getUserpassword();
                newSystemUser.setUserpassword(password);
            } else {
                String userPassword = auth.getUserPassword(newSystemUser);
                newSystemUser.setUserpassword(userPassword);
            }

            newSystemUser = em.merge(newSystemUser);

        }
    }

    public SystemUser find(SystemUser systemUser) {
        SystemUser result = em.find(SystemUser.class, systemUser.getUserid());
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<SystemUser> findRange(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SystemUser> cq = builder.createQuery(SystemUser.class);
        Root<SystemUser> from = cq.from(SystemUser.class);
        cq.select(from);

        if ((filters != null) && !filters.isEmpty()) {
            Set<Entry<String, Object>> es = filters.entrySet();
            List<Predicate> predicateList = new ArrayList<Predicate>();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                Predicate predicate;
                if ("deletedDate".equals(field)) {
                    predicate = builder.isNull(x);
                } else {
                    String pattern = filter.getValue() + "%";
                    x = builder.lower(x);
                    pattern = pattern.toLowerCase();
                    predicate = builder.like(x, pattern);
                }
                predicateList.add(predicate);
            }

            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);

        }
        if (sortField != null) {
            if (sortOrder.equals(SortOrder.ASCENDING)) {
                cq.orderBy(builder.asc(from.get(sortField)));
            } else if (sortOrder.equals(SortOrder.DESCENDING)) {
                cq.orderBy(builder.desc(from.get(sortField)));
            }
        }
        Query q = em.createQuery(cq);
        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        return q.getResultList();
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

            Expression<String> deletedDate = from.get("deletedDate");
            Predicate deletedPredicate = builder.isNull(deletedDate);
            predicateList.add(deletedPredicate);

            cq.where(predicates);
        }
        cq.orderBy(builder.asc(from.get("username")));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    public List<SystemUser> findAllExceptMe() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SystemUser> cq = builder.createQuery(SystemUser.class);
        Root<SystemUser> from = cq.from(SystemUser.class);
        cq.select(from);
        List<Predicate> predicateList = new ArrayList<Predicate>();

        Expression<String> x = from.get("userid");
        Predicate predicate = builder.notEqual(x, getLoggedInSystemUser().getUserid());
        predicateList.add(predicate);

        Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        cq.where(predicates);
        cq.orderBy(builder.asc(from.get("username")));
        Query q = em.createQuery(cq);
        return q.getResultList();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int count(Map<String, Object> actualFilters) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery cq = builder.createQuery();
        Root<SystemUser> from = cq.from(SystemUser.class);
        cq.select(builder.count(from));

        if ((actualFilters != null) && !actualFilters.isEmpty()) {
            Set<Entry<String, Object>> es = actualFilters.entrySet();
            List<Predicate> predicateList = new ArrayList<Predicate>();
            for (Entry<String, Object> filter : es) {
                String field = filter.getKey();
                Expression<String> x = from.get(field);
                Predicate predicate;
                if ("deletedDate".equals(field)) {
                    predicate = builder.isNull(x);
                } else {
                    String pattern = filter.getValue() + "%";
                    x = builder.lower(x);
                    pattern = pattern.toLowerCase();
                    predicate = builder.like(x, pattern);
                }
                predicateList.add(predicate);
            }

            Predicate predicates = builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            cq.where(predicates);

        }

        Query q = em.createQuery(cq);
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }

    public boolean isAdminRole() {
        boolean result = false;
        SystemUser user = getLoggedInSystemUser();
        if (user != null) {
            result = user.isAdminUser();
        }
        return result;
    }

    public SystemUser getLoggedInSystemUser() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession(false);
        SystemUser user = null;
        if (session != null) {
            user = (SystemUser) session.getAttribute(LOGGED_IN_SYSTEM_USER);
            if (user == null) {
                Principal principal = auth.getLoggedInPrincipal();
                if (principal != null) {
                    user = getLoggedInSystemUser(principal);
                    UserJoinGroup group = findUserJoinGroup(user.getEmailAddress());
                    if (group != null) {
                        user.setAdminUser(getRole(user).equals(ADMIN_GROUP));
                        session.setAttribute(LOGGED_IN_SYSTEM_USER, user);
                    }
                }
            }
        }
        return user;
    }

    public void removeSystemUser(SystemUser item) {
        item = find(item);
        if (item != null) {
            UserJoinGroup userJoinGroup = findUserJoinGroup(item.getEmailAddress());
            if (userJoinGroup != null) {
                em.remove(userJoinGroup);
            }
            em.remove(item);
        }
    }

    public boolean isAdmin() {
        return getLoggedInSystemUser().isAdminUser();
    }

    public boolean isCaseManager() {
        boolean result = (getLoggedInSystemUser() != null) && (getLoggedInSystemUser().getCaseManager());
        return result;
    }

    public String getRole(SystemUser user) {
        if (user != null) {
            UserJoinGroup group = findUserJoinGroup(user.getEmailAddress());
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
        UserJoinGroupId id = new UserJoinGroupId();
        id.setUserName(emailAddress);
        id.setGroupName(ADMIN_GROUP);
        UserJoinGroup result = em.find(UserJoinGroup.class, id);
        if (result == null) {
            id.setGroupName(USER_GROUP);
            result = em.find(UserJoinGroup.class, id);
        }
        return result;
    }

    public void removeUserJoinGroup(UserJoinGroup userJoinGroup) {
        if ((userJoinGroup != null) && (userJoinGroup.getUserJoinGroupId() != null)) {
            userJoinGroup = em.find(UserJoinGroup.class, userJoinGroup.getUserJoinGroupId());
            if (userJoinGroup != null) {
                em.remove(userJoinGroup);
            }
        }
    }

    public void mergeUserJoinGroup(UserJoinGroup userJoinGroup) {
        em.merge(userJoinGroup);
    }

    public void persistUserJoinGroup(UserJoinGroup userJoinGroup) {
        em.persist(userJoinGroup);
    }

    public SystemUser findByPIN(SystemUser systemUser) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery cq = builder.createQuery();
        Root<SystemUser> from = cq.from(SystemUser.class);
        cq.select(from);
        Expression<String> x = from.get("pinCode");
        Predicate predicate = builder.equal(x, systemUser.getPinCode());
        cq.where(predicate);

        Query q = em.createQuery(cq);
        List<SystemUser> list = q.getResultList();
        SystemUser result = list.get(0);
        return result;
    }

    public void mergeSystemUser(SystemUser item) {
        em.merge(item);
    }

    public void generateQRCode(SystemUser systemUser, String oFileName) throws WriterException, IOException {
        String contents = "PIN:" + systemUser.getPinCode() + "\tNAME:" + systemUser.getUsername();
        QRCodeGenerator.generateFile(systemUser.getUsername(), contents, oFileName);
    }

    public void removeUserGroups(SystemUser newSystemUser) {
        UserJoinGroupId userJoinGroupId = new UserJoinGroupId();
        userJoinGroupId.setGroupName(null);
        newSystemUser = em.find(newSystemUser.getClass(), newSystemUser.getUserid());
        userJoinGroupId.setUserName(newSystemUser.getEmailAddress());
        UserJoinGroup userJoinGroup = new UserJoinGroup();
        userJoinGroup.setUserJoinGroupId(userJoinGroupId);
        em.remove(userJoinGroup);
    }

    public Properties getUserSettings() {
        Properties props = new Properties();
        return props;
    }

    public String getUserSettingsValue(String propertyName) {
        Properties props = getUserSettings();
        return props.getProperty(propertyName);
    }

    public List<UserSettings> getUserSettings(Long userid) {
        Query q = em.createQuery("from UserSettings where userid=:userid");
        q.setParameter("userid", userid);
        @SuppressWarnings("unchecked")
        List<UserSettings> result = q.getResultList();
        return result;
    }

    public void persistUserProperties(SystemUser newSystemUser, Properties userProperties) {
        userProperties.forEach((k, v) -> {
            UserSettings userSettings = new UserSettings(newSystemUser.getUserid(), k, v);
            UserSettings stored = em.find(UserSettings.class, userSettings.getId());
            if (stored != null) {
                em.merge(userSettings);
            } else {
                em.persist(userSettings);
            }
        });
    }
	
    public SystemUser findByPIN(String pinCode) {
        SystemUser systemUser = new SystemUser();
        systemUser.setPinCode(pinCode);
        return findByPIN(systemUser);
    }

}
