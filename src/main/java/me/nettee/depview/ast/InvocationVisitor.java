package me.nettee.depview.ast;

import me.nettee.depview.model.Invocation;
import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvocationVisitor extends ASTVisitor {

    private Map<String, Set<Invocation>> invocationsMap = new HashMap<String, Set<Invocation>>();

    private void addInvocation(Invocation invocation) {
        String key = invocation.getQualifiedType();
        if (!invocationsMap.containsKey(key)) {
            invocationsMap.put(key, new HashSet<Invocation>());
        }
        Set<Invocation> invocations = invocationsMap.get(key);
        invocations.add(invocation);
    }

    public void printInvocations() {
        for (Map.Entry<String, Set<Invocation>> entry : invocationsMap.entrySet()) {
            String typeName = entry.getKey();
            Set<Invocation> invocations = entry.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append(typeName);
            sb.append(": {");
            for (Invocation invocation : invocations) {
                sb.append(invocation.getInvocationString());
                sb.append(", ");
            }
            sb.append("}");
            System.out.println("    " + sb.toString());
        }
    }

    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        SimpleName name = node.getName();
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                String typeName = typeBinding.getQualifiedName();
                Invocation invocation = new Invocation(expression.toString(), name.toString(), typeName);
                addInvocation(invocation);
            }
        }
        return true;
    }
}
