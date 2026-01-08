package com.example;

/**
 * Parent class extended by Groovy classes.
 * The AST transformation adds an annotated void method which triggers the bug.
 */
public class ParentClass {

    public void doSomething() {
        System.out.println("doing something");
    }

    public String getName() {
        return "ParentClass";
    }
}
