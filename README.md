# [GROOVY-11831](https://issues.apache.org/jira/browse/GROOVY-11831): Groovy 5 Compiler Bug Reproduction 
Minimal reproduction project for Groovy 5 compiler crash when an AST transformation adds an annotation (without explicit `@Target`) to a void method.

## Bug Description

Groovy 5 compiler crashes during class generation when:
1. An annotation has no explicit `@Target` (defaults to all targets including `TYPE_USE`)
2. An AST transformation adds this annotation to a generated method that returns `void`

### Error Message
```
BUG! exception in phase 'class generation' in source unit '...'
Adding type annotation @MyAnnotation to non-redirect node: void
```

## Project Structure

```
groovy-bug-repro/
├── java-lib/                          # Java library (compiled first)
│   └── src/main/java/com/example/
│       ├── MyAnnotation.java          # Annotation WITHOUT @Target
│       └── transform/
│           ├── AddVoidMethod.java     # Trigger annotation
│           └── AddVoidMethodTransformation.java  # AST transformation
└── groovy-app/                        # Groovy application
    └── src/main/groovy/com/example/
        └── ChildClass.groovy          # Uses @AddVoidMethod
```

## How to Reproduce

```bash
./gradlew clean :groovy-app:compileGroovy
```

## Root Cause

In `AddVoidMethodTransformation.java`, the AST transformation:
1. Creates a method with `ClassHelper.VOID_TYPE` return type
2. Adds `@MyAnnotation` to the method
3. Groovy's `ExtendedVerifier.extractTypeUseAnnotations()` tries to process `@MyAnnotation` as a TYPE_USE annotation
4. It attempts to add the annotation to the `void` return type
5. `void` is a "non-redirect node" in Groovy's AST, causing the crash

## Expected Behavior

The compiler should skip TYPE_USE annotation processing for `void` return types instead of crashing.

## Suggested Fix

In `ExtendedVerifier.java`, method `extractTypeUseAnnotations()` should check if the target type is void before calling `addTypeAnnotations()`:

```java
if (targetType == ClassHelper.VOID_TYPE) {
    return;
}
```

## Environment

- Groovy: 5.0.0
- Java: 17+
- Gradle: 8.x

## Related Issues

- [GROOVY-11479](https://issues.apache.org/jira/browse/GROOVY-11479): Similar crash with lambda parameters (fixed), but not for void return types

## Real-world Impact

This bug affects Micronaut Framework's Groovy support. The `@Internal` annotation in micronaut-core has no explicit `@Target` and is used in AST transformations that generate void methods.
