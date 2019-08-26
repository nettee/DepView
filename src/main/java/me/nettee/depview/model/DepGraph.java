package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import me.nettee.depview.main.Env;
import me.nettee.depview.main.Printer;
import me.nettee.depview.main.Settings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DepGraph {

    private static final Logger logger = LoggerFactory.getLogger(DepGraph.class);

    private final Env env;
    private MutableNetwork<PlainClass, DepAttr> depGraph;
    private Map<PlainClass, Integer> classSizes;
    private int allDepCount = 0;

    public DepGraph(Env env) {
        this.env = env;
        this.depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
        this.classSizes = new HashMap<>();
    }

    public void addDep(Dep<? extends DepAttr> invDep) {
        PlainClass fromClass = invDep.getFromClass();
        PlainClass toClass = invDep.getToClass();

        allDepCount++;

        if (!fromClass.isInPackage(env.getProjectPackage())) {
            return;
        }

        if (!toClass.isInPackage(env.getProjectPackage())) {
            return;
        }

        depGraph.addNode(fromClass);
        depGraph.addNode(toClass);

        depGraph.addEdge(fromClass, toClass, invDep.getAttr());
    }

    public void setClassSize(PlainClass class_, int size) {
        classSizes.put(class_, size);
    }

    public void santinize() {
        List<PlainClass> nodesToRemove = new ArrayList<>();
        for (PlainClass node : depGraph.nodes()) {
            if (!classSizes.containsKey(node)) {
                logger.warn("invalid node {}, remove it", node.toString());
                nodesToRemove.add(node);
            }
        }
        for (PlainClass node : nodesToRemove) {
            depGraph.removeNode(node);
        }
    }

    public void printDependencies() {

        Set<PlainClass> classes = depGraph.nodes();
        Set<DepAttr> dependencies = depGraph.edges();

        if (Settings.verbose) {

            logger.debug("All {} classes:{}", classes.size(), Printer.list(classes.stream()
                    .filter(class_ -> class_.isInPackage(env.getProjectPackage()))
                    .map(class_ -> class_.getShortName(env))));
            logger.debug("All {} dependencies:", dependencies.size());
            classes.forEach(fromClass -> {
                classes.forEach(toClass -> {
                    Set<DepAttr> depAttrs = depGraph.edgesConnecting(fromClass, toClass);
                    if (depAttrs.isEmpty()) {
                        return;
                    }
                    logger.debug("\t{} -> {}\t({}) {{}}",
                            padTo(fromClass.getShortName(env), 32),
                            padTo(toClass.getShortName(env), 34),
                            depAttrs.size(),
                            depAttrs.stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(", ")));
                });
            });
        } else {
            logger.info("Statistics:");
            logger.info("\t{} project classes", classes.size());
            logger.info("\t{} invocation dependencies between project classes (out of {})",
                    dependencies.size(), allDepCount);
        }
    }

    private static String padTo(String s, int len) {
        if (s.length() >= len) {
            return s;
        } else {
            return s + StringUtils.repeat(' ', len - s.length());
        }
    }

    public MutableNetwork<PlainClass, DepAttr> getGraph() {
        return depGraph;
    }

    public Map<PlainClass, Integer> getClassSizes() {
        return classSizes;
    }
}

