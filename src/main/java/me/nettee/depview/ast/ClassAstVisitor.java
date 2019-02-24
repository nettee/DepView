package me.nettee.depview.ast;

import me.nettee.depview.main.Env;
import me.nettee.depview.model.InvDep;
import me.nettee.depview.model.Invocation;
import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class ClassAstVisitor extends ASTVisitor {

    private final Env env;
    private final PlainClass thisClass;

    private List<InvDep> invDeps = new ArrayList<>();

    public ClassAstVisitor(Env env, PlainClass class_) {
        this.env = env;
        this.thisClass = class_;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        Type superclassType = node.getSuperclassType();
        if (superclassType != null) {
            ITypeBinding typeBinding = superclassType.resolveBinding();
            if (typeBinding != null) {
                PlainClass superClass = new PlainClass(typeBinding.getQualifiedName());
                System.out.println(String.format("Class %s extends %s",
                        thisClass.getShortName(env), superClass.getShortName(env)));
            } else {
                System.out.printf("Warning: no type binding for superclass %s - in class %s\n",
                        superclassType.toString(), thisClass.getShortName(env));
            }
        }
        List superInterfaceTypes = node.superInterfaceTypes();
        if (!superInterfaceTypes.isEmpty()) {
            for (Object o : superInterfaceTypes) {
                Type superInterfaceType = (Type) o;
                System.out.println(String.format("Class %s implements %s",
                        thisClass.getShortName(env), superInterfaceType.toString()));
            }
        }
        return true;
    }

    // For invocation-dependency, we care only MethodInvocation nodes
    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        SimpleName name = node.getName();
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                PlainClass targetClass = new PlainClass(typeBinding.getQualifiedName());
                Invocation invocation = new Invocation(expression.toString(), name.toString(), targetClass);
                InvDep invDep = new InvDep(thisClass, targetClass, invocation);
                invDeps.add(invDep);
            } else {
                System.out.printf("Warning: no type binding for %s.%s() - in class %s\n",
                        expression.toString(), name.toString(), thisClass.getShortName(env));
            }
        }
        return true;
    }

    public List<InvDep> getInvDeps() {
        return invDeps;
    }
}
