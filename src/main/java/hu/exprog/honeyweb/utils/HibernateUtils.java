package hu.exprog.honeyweb.utils;

import java.lang.reflect.Field;

import javax.persistence.Id;

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

    public static void copyObject(Object source, Object result, boolean includeVersion) {
        if (!source.getClass().equals(result.getClass())) {
            throw new ClassCastException("Copy to different type not allowed!");
        }
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ((!field.getName().startsWith("version") || includeVersion) && !field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(source);
                    if (fieldValue instanceof HibernateProxy) {
                        Object detachedFieldValue = ((HibernateProxy) fieldValue).getHibernateLazyInitializer().getImplementation();
                        field.set(result, detachedFieldValue);
                    } else {
                        field.set(result, fieldValue);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
