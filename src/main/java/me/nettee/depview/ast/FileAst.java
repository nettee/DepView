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
        PackageDeclaration packageDeclaration = compilationUnit.getPackage();
        String packageName = packageDeclaration.getName().getFullyQualifiedName();
        for (Object o : compilationUnit.types()) {
            TypeDeclaration typeDeclaration = (TypeDeclaration) o;
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String qualifiedClassName = String.format("%s.%s", packageName, className);
            System.out.println("className: " + qualifiedClassName);
        }
        return null;
    }

}
