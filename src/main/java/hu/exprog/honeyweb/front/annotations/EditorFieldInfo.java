package hu.exprog.honeyweb.front.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EditorFieldInfo {
    String fieldName() default "";
    String title() default "";
    boolean editable() default true;
    boolean visible() default true;
    boolean required() default false;
    int weight() default 1;
    Class<?> editorType() default Object.class;
}
