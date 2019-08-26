package me.nettee.depview.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAstVisitor;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.model.D3Graph;
import me.nettee.depview.model.DepGraph;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DepView {

    private static final Logger logger = LoggerFactory.getLogger(DepView.class);

    private final TestSubject testSubject;

    public DepView(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    public void view() {

        logger.info("Test subject: {}", testSubject.getName());

        List<Path> sources = testSubject.getSources();
        List<Path> classes = testSubject.getClasses();
        List<Path> jars = testSubject.getJars();

        checkNotNull(sources);
        checkNotNull(classes);
        checkState(!sources.isEmpty());
        checkState(!classes.isEmpty());

        logger.info(Printer.list("Sources", sources));
        logger.info(Printer.list("Classes", classes));
        logger.info(Printer.list("Jars", jars));

        // Note: classes (e.g. compiled bytecode files) are not used actually.
        // We only need to pass sources and jars to ASTCreator.
        // JDT takes jars as classpath entries.

        Env env = Env.newInstance(testSubject);

        ASTCreator astCreator = new ASTCreator(env, sources, jars);

        astCreator.stream()
                .map(FileAst::getClassAsts)
                .flatMap(Collection::stream)
                .forEach(classAst -> {
                    ClassAstVisitor visitor = new ClassAstVisitor(env, classAst.getPlainClass());
                    classAst.visitWith(visitor);
                });

        DepGraph depGraph = env.getDepGraph();
        depGraph.santinize();
        depGraph.printDependencies();
        printD3Js(depGraph);

        logger.info("Done.");
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

        for (String fileToCopy : new String[] {"index.html", "d3.v4.min.js", "force-directed.js"}) {
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

        logger.info("Dependencies dumped to {}.", dataFile.getPath());
        logger.info("Run `bin/serve.py {}' to view dependencies graph.", outputDir.getPath());
    }
}
