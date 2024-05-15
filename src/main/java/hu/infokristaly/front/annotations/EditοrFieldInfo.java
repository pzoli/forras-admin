package hu.infokristaly.front.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.faces.component.html.HtmlInputText;

@Target(value={ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EditοrFieldInfo {
    String fieldName() default "";
    String title() default "";
    boolean editable() default true;
    boolean visible() default true;
    boolean required() default false;
    int weight() default 1;
    Class<?> editorType() default Object.class;
}
