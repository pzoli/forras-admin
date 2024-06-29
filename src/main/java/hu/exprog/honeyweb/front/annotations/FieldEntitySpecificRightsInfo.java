package hu.exprog.honeyweb.front.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldEntitySpecificRightsInfo {
    String disabled() default "";
    String readOnly() default "";
    String visible() default "";
    String admin() default "";
}
