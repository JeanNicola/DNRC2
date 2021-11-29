package gov.mt.wris.annot;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface PermissionsNeeded{
    Permission[] value() default {};
}