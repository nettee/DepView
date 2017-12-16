package me.nettee.depview.main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DepView {

    private List<File> sourcePaths;
    private List<File> classPaths;

    public DepView(List<File> sourcePaths, List<File> classPaths) {
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

        List<File> sourcePaths = path.getStringList("sources").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());
        List<File> classPaths = path.getStringList("classes").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());

        System.out.println("sources: " + String.join(", ", sourcePaths.stream()
                .map(file -> file.getPath())
                .collect(Collectors.toList())));
        System.out.println("classes: " + String.join(", ", classPaths.stream().
                map(file -> file.getPath()).
                collect(Collectors.toList())));

//        if (testSubject.hasPath("dependency.jar")) {
//            List<? extends Config> jarDependencies = testSubject.getConfigList("dependency.jar");
//            for (Config jarDependency : jarDependencies) {
//                String jarPath = jarDependency.getString("path");
//                System.out.println("jar path: " + jarPath);
//            }
//        }

        DepView depView = new DepView(sourcePaths, classPaths);
        depView.view();
    }
}
