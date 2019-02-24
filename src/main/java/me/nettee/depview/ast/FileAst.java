package me.nettee.depview.ast;

import me.nettee.depview.main.Env;
import me.nettee.depview.model.PlainClass;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FileAst extends Ast {

    private final Env env;

    FileAst(Env env, ASTNode root) {
        super(root);
        this.env = env;
    }

    public List<ClassAst> getClassAsts() {

        List<ClassAst> asts = new ArrayList<>();

        CompilationUnit compilationUnit = (CompilationUnit) root;

        PackageDeclaration packageDeclaration = compilationUnit.getPackage();
        String packageName = packageDeclaration.getName().getFullyQualifiedName();

        for (Object o : compilationUnit.types()) {
            TypeDeclaration typeDeclaration = (TypeDeclaration) o;

            String className = typeDeclaration.getName().getFullyQualifiedName();
            String qualifiedClassName = String.format("%s.%s", packageName, className);

            PlainClass class_ = new PlainClass(qualifiedClassName);
            ClassAst classAst = new ClassAst(typeDeclaration, class_);
            asts.add(classAst);
        }

        return asts;
    }

}
