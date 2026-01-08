package com.example.transform;

import com.example.MyAnnotationWithTarget;
import com.example.MyAnnotationWithoutTarget;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.lang.reflect.Modifier;

/**
 * AST Transformation that adds a void method annotated with an annotation.
 *
 * By default, uses MyAnnotationWithTarget (the fixed version that works).
 * Set system property "useBuggyAnnotation=true" to use MyAnnotationWithoutTarget
 * which triggers the Groovy 5 bug.
 *
 * @see <a href="https://issues.apache.org/jira/browse/GROOVY-11831">GROOVY-11831</a>
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class AddVoidMethodTransformation extends AbstractASTTransformation {

    private static final ClassNode ANNOTATION_WITH_TARGET = ClassHelper.make(MyAnnotationWithTarget.class);
    private static final ClassNode ANNOTATION_WITHOUT_TARGET = ClassHelper.make(MyAnnotationWithoutTarget.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (nodes.length != 2 || !(nodes[1] instanceof ClassNode)) {
            return;
        }

        ClassNode classNode = (ClassNode) nodes[1];

        // Create: void generatedMethod() { println "generated" }
        BlockStatement body = new BlockStatement();
        body.addStatement(new ExpressionStatement(
            new MethodCallExpression(
                new VariableExpression("this"),
                "println",
                new ArgumentListExpression(new ConstantExpression("generated method called"))
            )
        ));

        MethodNode method = new MethodNode(
            "generatedMethod",
            Modifier.PUBLIC,
            ClassHelper.VOID_TYPE,  // void return type
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            body
        );

        // Choose annotation based on system property
        boolean useBuggy = Boolean.getBoolean("useBuggyAnnotation");
        ClassNode annotationToUse = useBuggy ? ANNOTATION_WITHOUT_TARGET : ANNOTATION_WITH_TARGET;

        // Add annotation to the void method
        // With ANNOTATION_WITHOUT_TARGET, this triggers the Groovy 5 bug!
        method.addAnnotation(new AnnotationNode(annotationToUse));

        classNode.addMethod(method);
    }
}
