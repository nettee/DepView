package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.ASTNode;

public class ClassAst extends Ast {

    private final String className;

    public ClassAst(ASTNode root, String className) {
        super(root);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
