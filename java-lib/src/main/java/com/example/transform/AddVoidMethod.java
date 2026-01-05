package com.example.transform;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that triggers AST transformation to add a void method
 * with @MyAnnotation (which has no @Target, defaulting to TYPE_USE).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@GroovyASTTransformationClass("com.example.transform.AddVoidMethodTransformation")
public @interface AddVoidMethod {
}
