package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import me.nettee.depview.main.Env;
import me.nettee.depview.main.Settings;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class DepGraph {

    private final Env env;
    private final MutableNetwork<PlainClass, DepAttr> depGraph;
    private int allDepCount = 0;

    public DepGraph(Env env) {
        this.env = env;
        this.depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
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
}

