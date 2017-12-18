package me.nettee.depview.ast;

import me.nettee.depview.model.InvDep;
import me.nettee.depview.model.Invocation;
import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class InvocationVisitor extends ASTVisitor {

    private final PlainClass thisClass;

    private List<InvDep> invDeps = new ArrayList<>();

    public InvocationVisitor(PlainClass class_) {
        this.thisClass = class_;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        SimpleName name = node.getName();
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                String typeName = typeBinding.getQualifiedName();
                PlainClass targetClass = new PlainClass(thisClass, typeName);
                Invocation invocation = new Invocation(expression.toString(), name.toString(), targetClass);
                InvDep invDep = new InvDep(thisClass, targetClass, invocation);
                invDeps.add(invDep);
            } else {
                System.out.printf("Warning: no type binding for %s.%s() - in class %s\n",
                        expression.toString(), name.toString(), thisClass.getShortName());
            }
        }
        return true;
    }

    public List<InvDep> getInvDeps() {
        return invDeps;
    }
}
