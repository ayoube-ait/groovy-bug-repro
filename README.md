# [GROOVY-11831](https://issues.apache.org/jira/browse/GROOVY-11831): Groovy 5 Compiler Bug Reproduction 
Minimal reproduction project for Groovy 5 compiler crash when an AST transformation adds an annotation (without explicit `@Target`) to a void method.

## Bug Description

Groovy 5 crashes during class generation when:
1. An annotation has no explicit `@Target` (Groovy incorrectly treats it as `TYPE_USE`)
2. An AST transformation adds this annotation to a generated method that returns `void`

### Error Message
```
BUG! exception in phase 'class generation' in source unit '...'
Adding type annotation @MyAnnotation to non-redirect node: void
```

## Root Cause

Groovy 5 violates **JLS 9.6.4.1** which states:

> "If an annotation of type java.lang.annotation.Target is not present on the declaration of an annotation interface A, then A is applicable in all declaration contexts and in **no type contexts**."

Groovy incorrectly treats annotations without `@Target` as if they include `TYPE_USE`, then attempts to process them on void return types, causing a crash.

## Quick Test

### Reproduce the Bug (fails)
```bash
./gradlew clean :groovy-app:compileGroovy -PuseBuggyAnnotation
```

### Verify the Fix (passes)
```bash
./gradlew clean :groovy-app:compileGroovy
```

## Project Structure

```
groovy-bug-repro/
├── java-lib/                              # Java library (compiled first)
│   └── src/main/java/com/example/
│       ├── MyAnnotationWithTarget.java    # FIXED: Has explicit @Target (excludes TYPE_USE)
│       ├── MyAnnotationWithoutTarget.java # BUGGY: No @Target (triggers Groovy bug)
│       └── transform/
│           ├── AddVoidMethod.java         # Trigger annotation
│           └── AddVoidMethodTransformation.java  # AST transformation
└── groovy-app/                            # Groovy application
    └── src/main/groovy/com/example/
        └── ChildClass.groovy              # Uses @AddVoidMethod
```

## The Fix

Add explicit `@Target` with all declaration contexts per JLS 9.6.4.1, excluding `TYPE_USE` and `TYPE_PARAMETER`:

```java
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
public @interface MyAnnotation { }
```

## Environment

- Groovy: 5.0.3
- Java: 17+
- Gradle: 8.x+

## Related Issues

- [GROOVY-11831](https://issues.apache.org/jira/browse/GROOVY-11831): Groovy 5 compiler crash with TYPE_USE annotations on void methods
- [GROOVY-11479](https://issues.apache.org/jira/browse/GROOVY-11479): Similar crash with lambda parameters (fixed in 5.0.0-alpha-11)

## Real-world Impact

This bug affects Micronaut Framework's Groovy support. The `@Internal` annotation in micronaut-core has no explicit `@Target` and is used in AST transformations that generate void methods, preventing Groovy function scripts from compiling with Micronaut 5 + Groovy 5.
