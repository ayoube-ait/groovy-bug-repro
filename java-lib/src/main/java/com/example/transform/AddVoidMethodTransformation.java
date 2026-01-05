package com.example.transform;

import com.example.MyAnnotation;
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
 * AST Transformation that adds a void method annotated with @MyAnnotation.
 * This triggers the Groovy 5 bug because @MyAnnotation has no @Target
 * (defaults to TYPE_USE) and the method returns void.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class AddVoidMethodTransformation extends AbstractASTTransformation {

    private static final ClassNode MY_ANNOTATION = ClassHelper.make(MyAnnotation.class);

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

        // Add @MyAnnotation to the void method - THIS TRIGGERS THE BUG!
        method.addAnnotation(new AnnotationNode(MY_ANNOTATION));

        classNode.addMethod(method);
    }
}
