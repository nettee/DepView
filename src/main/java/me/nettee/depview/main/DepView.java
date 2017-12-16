package me.nettee.depview.main;

import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DepView {

    private File[] sourcePaths;
    private File[] classPaths;

    public DepView() {
    }

    public void setSourcePaths(File[] sourcePaths) {
        this.sourcePaths = sourcePaths;
    }

    public void setClassPaths(File[] classPaths) {
        this.classPaths = classPaths;
    }

    public void view() {
        if (classPaths == null) {
            throw new NullPointerException();
        }

        ASTCreator astCreator = new ASTCreator(sourcePaths, classPaths);

        while (astCreator.hasNext()) {
            FileAst fileAst = astCreator.next();
            Iterable<ClassAst> classAsts = fileAst.getClassDeclarations();
            for (ClassAst classAst : classAsts) {
                System.out.println("class: " + classAst.getClassName());
                InvocationVisitor visitor = new InvocationVisitor();
                fileAst.visitWith(visitor);
                visitor.printInvocations();
            }
            System.out.println("------------------------------");
        }

        System.out.println("Done.");
    }

    public static void main(String[] args) {
        File projectDir = new File("/home/william/projects/java/astcomparator");
        File sourcePath = new File(projectDir, "src/main/java");
        File classPath = new File(projectDir, "target/classes");
        DepView depView = new DepView();
        depView.setSourcePaths(new File[]{sourcePath});
        depView.setClassPaths(new File[]{classPath});
        depView.view();
    }
}
