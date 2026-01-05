package com.example

import com.example.transform.AddVoidMethod

/**
 * Groovy class with @AddVoidMethod transformation.
 * The transformation adds a void method with @MyAnnotation.
 * This triggers Groovy 5 compiler bug:
 * "BUG! exception in phase 'class generation' ...
 *  Adding type annotation @MyAnnotation to non-redirect node: void"
 */
@AddVoidMethod
class ChildClass {

    static void main(String[] args) {
        def child = new ChildClass()
        child.generatedMethod()  // Call the generated method
    }
}
