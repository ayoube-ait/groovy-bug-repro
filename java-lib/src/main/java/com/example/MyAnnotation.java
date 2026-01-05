package com.example;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation WITHOUT explicit @Target.
 * This means it defaults to ALL element types including TYPE_USE.
 * This triggers a bug in Groovy 5 when used on void methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyAnnotation {
}
