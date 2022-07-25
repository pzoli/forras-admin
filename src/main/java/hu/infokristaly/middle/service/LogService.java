package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.primefaces.model.SortOrder;

import hu.infokristaly.back.model.LogEntry;
import hu.infokristaly.back.model.LoggedFunction;
import hu.infokristaly.back.model.SystemUser;

@Stateless
@Named
public class LogService {

    public static final byte CREATE = 1;
    public static final byte MODIFY = 2;
    public static final byte REMOVE = 3;
    public static final byte EXECUTE = 4;

    List<SimpleEntry<String, Object>> filterList = new LinkedList<SimpleEntry<String, Object>>();

    @Inject
    private EntityManager em;

    public void logUserActivity(SystemUser user, long function, byte action, Serializable data) {
        LogEntry logEntry = new LogEntry();
        logEntry.setLoggedByUser(user);
        logEntry.setAction(action);
        if (data != null) {
            logEntry.setLoggedValue(data.toString());
        }
        logEntry.setLogTimestamp(new Date());
        LoggedFunction loggedFunction = em.find(LoggedFunction.class, function);
        logEntry.setLoggedFunction(loggedFunction);
        em.persist(logEntry);
    }

    public LogEntry find(Long primaryKey) {
        return em.find(LogEntry.class, primaryKey);
    }

    private String buildQuery(Map<String, Object> filters) {
        StringBuffer result = new StringBuffer("from LogEntry e");
        /*
        filterList.clear();
        if ((filters != null) && (filters.size() > 0)) {
            result.append(" where");
            filters.entrySet().forEach(e -> {
                if (filterList.size() > 0) {
                    result.append(" and");
                }
                SimpleEntry<String, Object> filter = null;
                switch (e.getKey()) {
                case "loggedByUser.username":
                    result.append(" e.loggedByUser.username like :username");
                    String userName = (String) e.getValue();
                    filter = new SimpleEntry<String, Object>("username", userName + ((userName != null) && !userName.contains("%") ? "%": "") );
                    break;
                case "loggedFunction.functionName":
                    result.append(" e.loggedFunction.functionName like :functionName");
                    String value = (String) e.getValue();
                    filter = new SimpleEntry<String, Object>("functionName", value + ((value != null) && !value.contains("%") ? "%" : ""));
                    break;
                case "logTimestamp":
                    result.append(" e.logTimestamp >= :logTimestamp");
                    filter = new SimpleEntry<String, Object>("logTimestamp", e.getValue());
                    break;
                case "loggedValue":
                    result.append(" e." + e.getKey() + " like :" + e.getKey());
                    filter = new SimpleEntry<String, Object>(e.getKey(), e.getValue());
                    break;
                default:
                    result.append(" e." + e.getKey() + " = :" + e.getKey());
                    filter = new SimpleEntry<String, Object>(e.getKey(), e.getValue());
                }
                filterList.add(filter);
            });
        }
        */
        return result.toString();
    }

    public List<LogEntry> findRange(int first, int pageSize, String actualOrderField, SortOrder actualSortOrder, Map<String, Object> filters) {
        String sQuery = buildQuery(filters);
        if ((actualOrderField != null) && !actualOrderField.isEmpty()) {
            String field = "";
            if (actualOrderField.equalsIgnoreCase("log.loggedFunction.functionName")) {
                field = "e.loggedFunction.functionName";
            } else if (actualOrderField.equalsIgnoreCase("log.loggedByUser.username")) {
                field = "e.loggedByUser.username";
            } else {
                field = actualOrderField;
            }
            sQuery = sQuery + " order by " + field + (actualSortOrder == SortOrder.ASCENDING ? " asc" : " desc");
        }
        Query q = em.createQuery(sQuery);
/*
        filterList.forEach(e -> {
            Class<?> paramType = q.getParameter(e.getKey()).getParameterType();
            if (paramType != String.class) {
                try {
                    Object value;
                    if (e.getValue().getClass().equals(Date.class)) {
                        value = e.getValue();
                    } else {
                        Constructor<?> constructor = paramType.getConstructor(String.class);
                        value = constructor.newInstance(((String) e.getValue()));
                    }
                    q.setParameter(e.getKey(), value);                    
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                q.setParameter(e.getKey(), e.getValue());
            }
        });
*/
        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public Integer count(Map<String, Object> actualFilters) {
        String sQuery = buildQuery(actualFilters);
        sQuery = "select count(*) " + sQuery;
        Query q = em.createQuery(sQuery);
/*
        filterList.forEach(e -> {
            Class<?> paramType = q.getParameter(e.getKey()).getParameterType();
            if (paramType != String.class) {
                try {
                    Object value;
                    if (e.getValue().getClass().equals(Date.class)) {
                        value = e.getValue();
                    } else {
                        Constructor<?> constructor = paramType.getConstructor(String.class);
                        value = constructor.newInstance(((String) e.getValue()));
                    }
                    q.setParameter(e.getKey(), value);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                q.setParameter(e.getKey(), e.getValue());
            }
        });
*/
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }

}
