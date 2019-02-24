package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import me.nettee.depview.main.Env;
import me.nettee.depview.main.Settings;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DepGraph {

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
                System.out.printf("Warning: invalid node %s, remove it\n", node.toString());
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
            System.out.printf("All %d classes:\n", classes.size());
            classes.forEach(class_ -> {
                if (!class_.isInPackage(env.getProjectPackage())) {
                    return;
                }
                System.out.println("\t" + class_.getShortName(env));
            });
            System.out.printf("All %d dependencies:\n", dependencies.size());
            classes.forEach(fromClass -> {
                classes.forEach(toClass -> {
                    Set<DepAttr> depAttrs = depGraph.edgesConnecting(fromClass, toClass);
                    if (depAttrs.isEmpty()) {
                        return;
                    }
                    System.out.printf("\t%s -> %s\t(%d) {%s}\n",
                            padTo(fromClass.getShortName(env), 32),
                            padTo(toClass.getShortName(env), 34),
                            depAttrs.size(),
                            String.join(", ", depAttrs.stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toList())));
                });
            });
        } else {
            System.out.println("Statistics:");
            System.out.printf("\t%d project classes\n", classes.size());
            System.out.printf("\t%d invocation dependencies between project classes (out of %d)\n",
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

