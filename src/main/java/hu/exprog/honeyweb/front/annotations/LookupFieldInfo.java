package hu.exprog.honeyweb.front.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LookupFieldInfo {
    String labelField() default "";
    String keyField() default "";
    String service() default "";
    String sortField() default "";
    String filterField() default "";
	String detailDialogFile() default "";
	String converter() default "";
	String filterFunction() default "";
	String format() default "";
}
