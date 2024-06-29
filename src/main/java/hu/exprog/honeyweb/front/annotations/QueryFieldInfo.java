package hu.exprog.honeyweb.front.annotations;

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
    boolean readOnly() default false;
    boolean disabled() default false;
    boolean sortable() default true;
    boolean listable() default true;
    boolean admin() default true;
    int weight() default 1;
}
