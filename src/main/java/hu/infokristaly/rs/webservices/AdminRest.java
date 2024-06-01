package hu.infokristaly.rs.webservices;

import java.util.Set;
import java.util.HashSet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class AdminRest extends Application {

    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();
    public AdminRest(){
         singletons.add(new GetUserEvents());
         singletons.add(new AddClientToEvent());
         singletons.add(new GetClientsForEvent());
         singletons.add(new DeleteClientsFromEvent());
         singletons.add(new GetUserMessages());
         singletons.add(new CreateNFCLog());
    }
    @Override
    public Set<Class<?>> getClasses() {
         return empty;
    }
    @Override
    public Set<Object> getSingletons() {
         return singletons;
    }
}
