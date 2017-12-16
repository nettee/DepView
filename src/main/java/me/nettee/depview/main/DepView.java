package me.nettee.depview.main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
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

    public DepView(File[] sourcePaths, File[] classPaths) {
        this.sourcePaths = sourcePaths;
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

//        String filename = "astcomparator.conf";
        String filename = "aql-client.conf";

        File testSubjectConfigFile = new File(filename);
        Config conf = ConfigFactory.parseFile(testSubjectConfigFile);

        Config testSubject = conf.getConfig("testSubject");
        System.out.println("project name: " + testSubject.getString("name"));

        Config path = testSubject.getConfig("path");

        File projectDir = new File(path.getString("base"));
        File sourcePath = new File(projectDir, path.getString("source"));
        File classPath = new File(projectDir, path.getString("classes"));

        DepView depView = new DepView(new File[]{sourcePath}, new File[]{classPath});
        depView.view();
    }
}
