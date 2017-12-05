package me.nettee.depview;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class Ast {

    private final ASTNode root;

    Ast(ASTNode root) {
        this.root = root;
    }

    public void visitWith(ASTVisitor visitor) {
        root.accept(visitor);
    }

}
