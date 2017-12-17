package me.nettee.depview.main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.JarsFinder;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DepView {

    private final List<File> sourcePaths;
    private final List<File> classPaths;
    private final List<File> jars;

    public DepView(List<File> sourcePaths, List<File> classPaths) {
        this.sourcePaths = sourcePaths;
        this.classPaths = classPaths;
        this.jars = new ArrayList<>();
    }

    public void addJars(List<File> jars) {
        this.jars.addAll(jars);
    }

    public void view() {
        if (classPaths == null) {
            throw new NullPointerException();
        }

        ASTCreator astCreator = new ASTCreator(sourcePaths, classPaths);

//        while (astCreator.hasNext()) {
//            FileAst fileAst = astCreator.next();
//            Iterable<ClassAst> classAsts = fileAst.getClassDeclarations();
//            for (ClassAst classAst : classAsts) {
//                System.out.println("class: " + classAst.getClassName());
//                InvocationVisitor visitor = new InvocationVisitor();
//                fileAst.visitWith(visitor);
//                visitor.printInvocations();
//            }
//            System.out.println("------------------------------");
//        }

        System.out.println("Done.");
    }

    public static void main(String[] args) {

        String filename = "astcomparator.conf";
//        String filename = "aql-client.conf";

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

        DepView depView = new DepView(sourcePaths, classPaths);

        if (testSubject.hasPath("dependency.jar")) {
            List<File> jars = testSubject.getStringList("dependency.jar").stream()
                    .map(jarDependency -> new File(projectDir, jarDependency))
                    .collect(Collectors.toList());
            for (File jar : jars) {
                System.out.println("jar path: " + jar.getPath());
            }
            depView.addJars(jars);
        }

        if (testSubject.hasPath("dependency.maven")) {
            Config mavenDependency = testSubject.getConfig("dependency.maven");
            File repository = new File(mavenDependency.getString("repository"));
            System.out.println("repository: " + repository);
            List<String> jarDependencies = mavenDependency.getStringList("jar");
            Pair<Map<String, File>, Set<String>> result = JarsFinder.find(repository, jarDependencies);
            Map<String, File> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();
            System.out.println("Jars found:");
            jarsFound.forEach((fileName, file) -> System.out.println("\t" + file.getAbsolutePath()));
            System.out.println("Jars not found:");
            jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
        }

        depView.view();
    }
}
