package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class FileAst extends Ast {

    FileAst(ASTNode root) {
        super(root);
    }

    public Iterable<ClassAst> getClassAsts() {

        List<ClassAst> asts = new ArrayList<ClassAst>();

        CompilationUnit compilationUnit = (CompilationUnit) root;

        PackageDeclaration packageDeclaration = compilationUnit.getPackage();
        String packageName = packageDeclaration.getName().getFullyQualifiedName();

        for (Object o : compilationUnit.types()) {
            TypeDeclaration typeDeclaration = (TypeDeclaration) o;
            String className = typeDeclaration.getName().getFullyQualifiedName();
            String qualifiedClassName = String.format("%s.%s", packageName, className);

            ClassAst classAst = new ClassAst(typeDeclaration, qualifiedClassName);
            asts.add(classAst);
        }

        return asts;
    }

}
