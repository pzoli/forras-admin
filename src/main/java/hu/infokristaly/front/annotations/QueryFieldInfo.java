package hu.infokristaly.front.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryFieldInfo {
    String fieldName() default "";
    String title() default "";
    boolean visible() default true;
    boolean sortable() default true;
    int weight() default 1;
}
