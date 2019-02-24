package me.nettee.depview.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;
import me.nettee.depview.model.D3Graph;
import me.nettee.depview.model.DepGraph;
import me.nettee.depview.model.InvDep;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DepView {

    private final TestSubject testSubject;

    public DepView(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    public void view() {

        System.out.println("Test subject: " + testSubject.getName());
        String projectPackage = testSubject.getProjectPackage();

        List<Path> sources = testSubject.getSources();
        List<Path> classes = testSubject.getClasses();
        List<Path> jars = testSubject.getJars();

        checkNotNull(sources);
        checkNotNull(classes);
        checkState(!sources.isEmpty());
        checkState(!classes.isEmpty());

        Consumer<Path> filePathPrinter = path -> System.out.println("\t" + path.toString());
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
            for (ClassAst classAst : fileAst.getClassAsts()) {
                InvocationVisitor visitor = new InvocationVisitor(classAst.getPlainClass());

                classAst.visitWith(visitor);

                List<InvDep> invDeps = visitor.getInvDeps();
                invDeps.forEach(depGraph::addDep);
            }
        }

        depGraph.printDependencies();
        printD3Js(depGraph);

        System.out.println("Done.");
    }

    private void printD3Js(DepGraph depGraph) {

        D3Graph graph = D3Graph.fromDepGraph(depGraph);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(graph);

        File output = new File("output");
        if (!output.exists()) {
            output.mkdir();
        }

        String safeName = StringUtils.replaceAll(testSubject.getName(), "[ \\s/\\\\~`?*^&$#@%]", "-");
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.printf("Dependencies dumped to %s.\n", dataFile.getPath());
        System.out.printf("Run `%s %s' to view dependencies graph.\n", "bin/serve", outputDir.getPath());
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
