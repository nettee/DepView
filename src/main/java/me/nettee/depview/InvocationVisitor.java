package me.nettee.depview;

import org.eclipse.jdt.core.dom.*;

public class InvocationVisitor extends ASTVisitor {

    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        SimpleName name = node.getName();
        System.out.printf("Method invocation: %s.%s() --- ", expression, name);
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding == null) {
                System.out.println("Cannot resolve type binding");
            } else {
                String typeName = typeBinding.getQualifiedName();
                System.out.printf("type name: %s\n", typeName);
            }
        } else {
            System.out.println();
        }

        return true;
    }
}
