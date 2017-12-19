package me.nettee.depview.main;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;
import me.nettee.depview.model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;

public class DepView {

    private final TestSubject testSubject;

    public DepView(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    public void view() {

        System.out.println("Test subject: " + testSubject.getName());
        String projectPackage = testSubject.getProjectPackage();
        System.out.println("Package name: " + projectPackage);

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

        ASTCreator astCreator = new ASTCreator(sources, jars, projectPackage);

        DepGraph depGraph = new DepGraph();

        while (astCreator.hasNext()) {
            FileAst fileAst = astCreator.next();
            Iterable<ClassAst> classAsts = fileAst.getClassAsts();

            for (ClassAst classAst : classAsts) {
                InvocationVisitor visitor = new InvocationVisitor(classAst.getPlainClass());

                classAst.visitWith(visitor);

                List<InvDep> invDeps = visitor.getInvDeps();
                invDeps.forEach(invDep -> {
                    depGraph.addDep(invDep);
                });
            }
        }

        depGraph.printDependencies();

        D3Graph graph = D3Graph.fromDepGraph(depGraph);

        Gson gson = new Gson();
        String json = gson.toJson(graph);

        File output = new File("output");
        if (!output.exists()) {
            output.mkdir();
        }

        String safeName = StringUtils.replaceAll(testSubject.getName(), "[ \\s/\\\\~`?*^&$#@%]", " ");
        File outputDir = new File(output, safeName);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        for (String fileToCopy : new String[] {"index.html", "d3.v4.min.js"}) {
            InputStream inputStream = getClass().getResourceAsStream("/" + fileToCopy);
            File indexFile = new File(outputDir, fileToCopy);
            try {
                Files.copy(inputStream, indexFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File dataFile = new File(outputDir, "graph.json");
        try (PrintWriter writer = new PrintWriter(dataFile)) {
            writer.println(json);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Done.");
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            throw new IllegalArgumentException();
        }
        String filename = args[0];

        File testSubjectConfigFile = new File(filename);

        Config conf = ConfigFactory.parseFile(testSubjectConfigFile);
        Config config = conf.getConfig("testSubject");

        TestSubject testSubject = TestSubject.fromConfig(config);

        DepView depView = new DepView(testSubject);
        depView.view();
    }

}
