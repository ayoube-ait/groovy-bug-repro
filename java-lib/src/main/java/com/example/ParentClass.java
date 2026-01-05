package com.example;

/**
 * Parent class with a void method annotated with @MyAnnotation.
 * When a Groovy class extends this, Groovy 5 crashes trying to
 * process the annotation as a TYPE_USE on the void return type.
 */
public class ParentClass {

    @MyAnnotation
    public void doSomething() {
        System.out.println("doing something");
    }

    public String getName() {
        return "ParentClass";
    }
}
