package hu.infokristaly.utils;

import java.lang.reflect.Field;

import org.hibernate.proxy.HibernateProxy;

public class HibernateUtils {

    public static void detachEntity(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(entity);
                if (fieldValue instanceof HibernateProxy) {
                    Object detachedFieldValue = ((HibernateProxy) fieldValue).getHibernateLazyInitializer().getImplementation();
                    field.set(entity, detachedFieldValue);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
