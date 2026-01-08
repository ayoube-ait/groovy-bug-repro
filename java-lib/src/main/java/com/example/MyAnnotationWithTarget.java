package com.example;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation WITH explicit @Target per JLS 9.6.4.1.
 *
 * Includes all declaration contexts, excludes TYPE_USE and TYPE_PARAMETER.
 * This matches the JLS default behavior for annotations without @Target.
 *
 * Use this annotation to verify the FIX works.
 *
 * @see <a href="https://issues.apache.org/jira/browse/GROOVY-11831">GROOVY-11831</a>
 */
@Target({
    ElementType.TYPE,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.CONSTRUCTOR,
    ElementType.LOCAL_VARIABLE,
    ElementType.ANNOTATION_TYPE,
    ElementType.PACKAGE,
    ElementType.MODULE,
    ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyAnnotationWithTarget {
}
