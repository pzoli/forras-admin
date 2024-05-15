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

@Named
@Stateless
public class RFIDCardService extends BasicService<RFIDCard> implements Serializable {

    private static final long serialVersionUID = -9139027636913174016L;

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    public RFIDCardService() {

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
        queryBuff.append("from RFIDCard");
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
    public RFIDCard find(RFIDCard item) {
        return em.find(RFIDCard.class, item.getId());
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Class<RFIDCard> getDomainClass() {
        return RFIDCard.class;
    }

    public RFIDCard findByRFID(RFIDCard rFIDCard) {
        Query q = em.createQuery("from RFIDCard where rfid = :rfid and type = :type");
        q.setParameter("rfid", rFIDCard.getRfid());
        q.setParameter("type", rFIDCard.getType());
        List<RFIDCard> result = q.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
