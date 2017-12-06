package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class FileAst extends Ast {

    FileAst(ASTNode root) {
        super(root);
    }

}
