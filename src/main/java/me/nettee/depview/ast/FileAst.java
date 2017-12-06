package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class FileAst extends Ast {

    FileAst(ASTNode root) {
        super(root);
    }

    public Iterable<Ast> getClassDeclarations() {
        List<Ast> asts = new ArrayList<Ast>();
        CompilationUnit compilationUnit = (CompilationUnit) root;
        for (Object o : compilationUnit.types()) {
            TypeDeclaration typeDeclaration = (TypeDeclaration) o;
            SimpleName name = typeDeclaration.getName();
            ITypeBinding typeBinding = name.resolveTypeBinding();
            String typeName = typeBinding.getQualifiedName();
            System.out.println("class: " + typeName);
        }
        return null;
    }

}
