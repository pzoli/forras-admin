package hu.exprog.honeyweb.front.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD,ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityFieldInfo {
    String info() default "";
    String alternateName() default "";
    int weight() default 1;
    String editor() default "";    	
    boolean required() default false;
    boolean listable() default true;
    Class<?> expected() default void.class;
	boolean preProcess() default false;
	boolean postProcess() default false;
	String converter() default "";
	String format() default "";
}