package me.nettee.depview.ast;

import me.nettee.depview.main.Env;
import me.nettee.depview.model.*;
import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class ClassAstVisitor extends ASTVisitor {

    private final Env env;
    private final PlainClass thisClass;

    private List<Dep<Invocation>> invDeps = new ArrayList<>();
    private List<Dep<Inheritation>> inhDeps = new ArrayList<>();

    public ClassAstVisitor(Env env, PlainClass class_) {
        this.env = env;
        this.thisClass = class_;
    }

    private void addDep(Dep<? extends DepAttr> dep) {
        DepGraph depGraph = env.getDepGraph();
        depGraph.addDep(dep);
        if (dep.getAttr() instanceof Inheritation) {
            Inheritation inheritation = (Inheritation) dep.getAttr();
            System.out.printf("%s %s %s\n",
                    dep.getFromClass().getShortName(env),
                    inheritation,
                    dep.getToClass().getShortName(env));
        } else if (dep.getAttr() instanceof Aggregation) {
            System.out.printf("%s aggregates %s\n",
                    dep.getFromClass().getShortName(env),
                    dep.getToClass().getShortName(env));
        }
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        Type superclassType = node.getSuperclassType();
        if (superclassType != null) {
            ITypeBinding typeBinding = superclassType.resolveBinding();
            if (typeBinding != null) {
                PlainClass superClass = new PlainClass(typeBinding);
                addDep(new Dep<>(thisClass, superClass, Inheritation.classExtends()));
            } else {
                System.out.printf("Warning: no type binding for superclass %s - in class %s\n",
                        superclassType.toString(), thisClass.getShortName(env));
            }
        }
        List superInterfaceTypes = node.superInterfaceTypes();
        if (!superInterfaceTypes.isEmpty()) {
            for (Object o : superInterfaceTypes) {
                Type superInterfaceType = (Type) o;
                ITypeBinding typeBinding = superInterfaceType.resolveBinding();
                if (typeBinding != null) {
                    PlainClass superClass = new PlainClass(typeBinding);
                    Inheritation inh = node.isInterface()
                            ? Inheritation.interfaceExtends()
                            : Inheritation.classImplements();
                    addDep(new Dep<>(thisClass, superClass, inh));
                } else {
                    System.out.printf("Warning: no type binding for super interface %s - in class %s\n",
                            superInterfaceType.toString(), thisClass.getShortName(env));
                }
            }
        }
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        Type fieldType = node.getType();
        ITypeBinding typeBinding = fieldType.resolveBinding();
        if (typeBinding != null) {
            PlainClass fieldClass = new PlainClass(typeBinding);
            addDep(new Dep<>(thisClass, fieldClass, new Aggregation()));
        } else {
            System.out.printf("Warning: no type binding for field %s - in class %s\n",
                    node.toString(), thisClass.getShortName(env));
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
                PlainClass targetClass = new PlainClass(typeBinding);
                Invocation invocation = new Invocation(expression.toString(), name.toString());
                addDep(new Dep<>(thisClass, targetClass, invocation));
            } else {
                System.out.printf("Warning: no type binding for %s.%s() - in class %s\n",
                        expression.toString(), name.toString(), thisClass.getShortName(env));
            }
        }
        return true;
    }

    public List<Dep<Invocation>> getInvDeps() {
        return invDeps;
    }

    public List<Dep<Inheritation>> getInhDeps() {
        return inhDeps;
    }
}
