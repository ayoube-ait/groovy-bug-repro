package com.example;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation WITHOUT explicit @Target.
 *
 * Per JLS 9.6.4.1: "If @Target is not present, the annotation is applicable
 * in all declaration contexts and in NO type contexts."
 *
 * However, Groovy 5 incorrectly treats this as including TYPE_USE,
 * causing a compiler crash when used on void methods.
 *
 * Use this annotation to REPRODUCE the bug.
 *
 * @see <a href="https://issues.apache.org/jira/browse/GROOVY-11831">GROOVY-11831</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyAnnotationWithoutTarget {
}
