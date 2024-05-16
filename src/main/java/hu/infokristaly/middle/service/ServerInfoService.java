package hu.infokristaly.middle.service;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.AbstractMap.SimpleEntry;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.primefaces.model.SortOrder;

import hu.infokristaly.back.model.QrtzJobDetail;

@Named
@Stateless
public class ServerInfoService implements Serializable {

    private static final long serialVersionUID = 1024885551824890762L;

    @Inject
    private EntityManager em;

    List<SimpleEntry<String, Object>> filterList = new LinkedList<SimpleEntry<String, Object>>();

    @PostConstruct
    public void init() {

    }
    
    public Properties getSystemDBInfo() {
        Properties props = new Properties();
        org.hibernate.engine.spi.SessionImplementor sessionImp = (org.hibernate.engine.spi.SessionImplementor) em.getDelegate();
        Connection connection = sessionImp.connection();
        try {         
            String dataBaseUrl = connection.getMetaData().getURL();
            String dataBaseProductName = connection.getMetaData().getDatabaseProductName();
            String dataBaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            String driverName = connection.getMetaData().getDriverName();
            String driverVersion = connection.getMetaData().getDriverVersion();
            int major = connection.getMetaData().getDatabaseMajorVersion();
            int minor = connection.getMetaData().getDatabaseMinorVersion();
            
            props.put("BaseUrl",dataBaseUrl);            
            props.put("BaseProductName",dataBaseProductName + " " + dataBaseProductVersion);
            props.put("DriverName",driverName);
            props.put("DriverVersion",driverVersion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return props;
    }

    public Properties getSystemAppServerInfo() throws Exception {
        Properties props = new Properties();
        try (ModelControllerClient client = ModelControllerClient.Factory.create("localhost", 9990)) {
            ModelNode op = Operations.createOperation("product-info", new ModelNode().setEmptyList());
            ModelNode result = client.execute(op);
            boolean found = Operations.isSuccessfulOutcome(result);
            if (!found) {
                op = Operations.createReadResourceOperation(new ModelNode().setEmptyList());
                result = client.execute(op);
                found = Operations.isSuccessfulOutcome(result);
            }
            if (found) {
                final ModelNode model = Operations.readResult(result);
                String productName = null;
                String productVersion = null;
                if (model.getType().equals(ModelType.LIST)) {
                    final ModelNode model0 = model.get(0);
                    if (model0.getType().equals(ModelType.OBJECT)) {
                        final ModelNode summary = model0.get("summary");

                        if (summary.hasDefined("product-name")) {
                            productName = summary.get("product-name").asString();
                        } else {
                            productName = "WildFly";
                        }

                        if (summary.hasDefined("product-version")) {
                            productVersion = summary.get("product-version").asString();
                        }

                    }
                } else {
                    if (model.hasDefined("product-name")) {
                        productName = model.get("product-name").asString();
                    }
                    if (model.hasDefined("product-version")) {
                        productVersion = model.get("product-version").asString();
                    }
                }
                StringBuffer info = new StringBuffer();
                props.put("Product", productName);
                props.put("Product Version", productVersion);

            } else {
                System.out.println(result);
                System.out.println(Operations.getFailureDescription(result));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return props;
    }

    public QrtzJobDetail find(Long primaryKey) {
        return em.find(QrtzJobDetail.class, primaryKey);
    }

    private String buildQuery(Map<String, Object> filters) {
        StringBuffer result = new StringBuffer("from QrtzJobDetail d");         
        return result.toString();
    }

    public List<QrtzJobDetail> findRange(int first, int pageSize, String actualOrderField, SortOrder actualSortOrder, Map<String, Object> filters) {
        String sQuery = buildQuery(filters);
        Query q = em.createQuery(sQuery);

        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        return q.getResultList();
    }

    public Integer count(Map<String, Object> actualFilters) {
        String sQuery = buildQuery(actualFilters);
        sQuery = "select count(*) " + sQuery;
        Query q = em.createQuery(sQuery);
        int result = ((Long) q.getSingleResult()).intValue();
        return result;
    }
}
