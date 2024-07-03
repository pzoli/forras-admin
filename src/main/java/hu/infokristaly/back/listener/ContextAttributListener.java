package hu.infokristaly.back.listener;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import hu.infokristaly.back.model.AppProperties;

public class ContextAttributListener implements ServletContextListener {
    @Inject
    private AppProperties appProperties;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String result = sce.getServletContext().getInitParameter("javax.faces.PROJECT_STAGE");
        appProperties.setProjectStage(result);        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        
    }    

}
