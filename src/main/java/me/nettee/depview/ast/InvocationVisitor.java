package me.nettee.depview.ast;

import me.nettee.depview.model.Invocation;
import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InvocationVisitor extends ASTVisitor {

    private Map<String, Set<Invocation>> invocationsMap = new HashMap<>();

    private void addInvocation(Invocation invocation) {
        String key = invocation.getQualifiedType();
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
//            {
//                ITypeBinding binding = name.resolveTypeBinding();
//                if (binding != null) {
//                    System.out.printf("Type binding of identifier %s: %s\n", name, binding.getQualifiedName());
//                }
//            }
//            {
//                IMethodBinding binding = node.resolveMethodBinding();
//                if (binding != null) {
//                    System.out.printf("Method binding of invocation %s.%s(): %s\n",
//                            expression.toString(),
//                            name.toString(),
//                            binding.getName());
//                }
//            }
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                String typeName = typeBinding.getQualifiedName();
                Invocation invocation = new Invocation(expression.toString(), name.toString(), typeName);
                addInvocation(invocation);
            } else {
                System.out.printf("\tno type binding for %s.%s()\n", expression.toString(), name.toString());
            }
        }
        return true;
    }
}
