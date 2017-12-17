package me.nettee.depview.ast;

import me.nettee.depview.model.Invocation;
import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationVisitor extends ASTVisitor {

    private final PlainClass class_;

    private Map<PlainClass, Set<Invocation>> invocationsMap = new HashMap<>();

    public InvocationVisitor(PlainClass className) {
        this.class_ = className;
    }

    private void addInvocation(Invocation invocation) {
        PlainClass key = invocation.getType();
        if (!invocationsMap.containsKey(key)) {
            invocationsMap.put(key, new HashSet<>());
        }
        Set<Invocation> invocations = invocationsMap.get(key);
        invocations.add(invocation);
    }

    public void printInvocations() {
        invocationsMap.forEach((typeName, invocations) -> System.out.printf("\t%s: {%s}\n", typeName,
                String.join(", ", invocations.stream().
                        map(invocation -> invocation.getInvocationString()).
                        collect(Collectors.toList()))));
    }

    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        SimpleName name = node.getName();
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                String typeName = typeBinding.getQualifiedName();
                PlainClass type = new PlainClass(typeName);
                Invocation invocation = new Invocation(expression.toString(), name.toString(), type);
                addInvocation(invocation);
            } else {
                System.out.printf("Warning: no type binding for %s.%s() - in class %s\n",
                        expression.toString(), name.toString(), class_.getName());
            }
        }
        return true;
    }

    public Map<PlainClass, Set<Invocation>> getInvocationsMap() {
        return invocationsMap;
    }
}
