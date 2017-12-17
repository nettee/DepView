package me.nettee.depview.ast;

import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.ASTNode;

public class ClassAst extends Ast {

    private final PlainClass class_;

    public ClassAst(ASTNode root, PlainClass class_) {
        super(root);
        this.class_ = class_;
    }

    public PlainClass getPlainClass() {
        return class_;
    }
}
