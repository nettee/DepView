package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class Ast {

    protected final ASTNode root;

    public Ast(ASTNode root) {
        this.root = root;
    }

    public void visitWith(ASTVisitor visitor) {
        root.accept(visitor);
    }
}
