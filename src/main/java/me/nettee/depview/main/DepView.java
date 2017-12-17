package me.nettee.depview.main;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;
import me.nettee.depview.model.Invocation;
import me.nettee.depview.model.PlainClass;
import me.nettee.depview.model.TestSubject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DepView {

    private final TestSubject testSubject;

    public DepView(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    private void showDependencies(Network<PlainClass, Invocation> depGraph) {

        System.out.println("all classes:");
        Set<PlainClass> classes = depGraph.nodes();
        classes.forEach(c -> System.out.println("\t" + c));

        System.out.println("all dependencies:");
        classes.forEach(inClass -> {
            classes.forEach(outClass -> {
                Set<Invocation> invocations = depGraph.edgesConnecting(inClass, outClass);
                if (!invocations.isEmpty()) {
                    System.out.printf("\t%s -> %s\t\t\t(%d) {%s}\n",
                            inClass.getName(),
                            outClass.getName(),
                            invocations.size(),
                            String.join(", ", invocations.stream()
                                    .map(invocation -> invocation.getInvocationString())
                                    .collect(Collectors.toList())));
                }
            });
        });
    }

    public void view() {

        List<File> sources = testSubject.getSources();
        List<File> classes = testSubject.getClasses();
        List<File> jars = testSubject.getJars();

        checkNotNull(sources);
        checkNotNull(classes);

        Consumer<File> filePathPrinter = file -> System.out.println("\t" + file.getPath());
        System.out.println("Sources:");
        sources.forEach(filePathPrinter);
        System.out.println("Classes:");
        classes.forEach(filePathPrinter);
        System.out.println("Jars:");
        jars.forEach(filePathPrinter);

//        classes.addAll(jars);

        ASTCreator astCreator = new ASTCreator(sources, jars);

        MutableNetwork<PlainClass, Invocation> depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();

        while (astCreator.hasNext()) {
            FileAst fileAst = astCreator.next();
            Iterable<ClassAst> classAsts = fileAst.getClassAsts();

            for (ClassAst classAst : classAsts) {
                PlainClass inClass = classAst.getPlainClass();
                InvocationVisitor visitor = new InvocationVisitor(inClass);
                classAst.visitWith(visitor);

                Map<PlainClass, Set<Invocation>> invocationsMap = visitor.getInvocationsMap();
                depGraph.addNode(inClass);
                invocationsMap.forEach((outClass, invocations) -> {
                    depGraph.addNode(outClass);
                    invocations.forEach(invocation -> {
                        depGraph.addEdge(inClass, outClass, invocation);
                    });
                });
            }
        }

        showDependencies(depGraph);

        System.out.println("Done.");
    }

    public static void main(String[] args) {

//        String filename = "astcomparator.conf";
        String filename = "aql-client.conf";

        File testSubjectConfigFile = new File(filename);
        TestSubject testSubject = parseConf(testSubjectConfigFile);

        DepView depView = new DepView(testSubject);
        depView.view();
    }

    private static TestSubject parseConf(File confFile) {

        Config conf = ConfigFactory.parseFile(confFile);
        Config config = conf.getConfig("testSubject");

        TestSubject testSubject = new TestSubject();
        testSubject.setName(config.getString("name"));

        Config path = config.getConfig("path");
        File projectDir = new File(path.getString("base"));

        List<File> sourcePaths = path.getStringList("sources").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());
        List<File> classPaths = path.getStringList("classes").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());

        testSubject.setSources(sourcePaths);
        testSubject.setClasses(classPaths);

        if (config.hasPath("dependency.jar")) {
            List<File> jars = config.getStringList("dependency.jar").stream()
                    .map(jarDependency -> new File(projectDir, jarDependency))
                    .collect(Collectors.toList());
            testSubject.addJars(jars);
        }

        if (config.hasPath("dependency.jdk")) {
            Config jdkDependency = config.getConfig("dependency.jdk");
            File jdkHome = new File(jdkDependency.getString("home"));

            List<String> jarDependencies = jdkDependency.getStringList("jar");
            Pair<Map<String, File>, Set<String>> result = JarsFinder.find(jdkHome, jarDependencies);
            Map<String, File> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        if (config.hasPath("dependency.maven")) {
            Config mavenDependency = config.getConfig("dependency.maven");
            File repository = new File(mavenDependency.getString("repository"));

            List<String> jarDependencies = mavenDependency.getStringList("jar");
            Pair<Map<String, File>, Set<String>> result = JarsFinder.find(repository, jarDependencies);
            Map<String, File> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        return testSubject;
    }
}
