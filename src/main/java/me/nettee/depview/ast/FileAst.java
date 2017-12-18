package me.nettee.depview.ast;

import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class FileAst extends Ast {

    private final String projectPackage;

    FileAst(ASTNode root, String projectPackage) {
        super(root);
        this.projectPackage = projectPackage;
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

            PlainClass class_ = new PlainClass(qualifiedClassName, projectPackage);
            ClassAst classAst = new ClassAst(typeDeclaration, class_);
            asts.add(classAst);
        }

        return asts;
    }

}
