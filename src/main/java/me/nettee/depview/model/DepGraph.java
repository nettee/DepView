package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import me.nettee.depview.main.Settings;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DepGraph {

    private final MutableNetwork<PlainClass, Invocation> depGraph;
    private int allDepCount = 0;

    public DepGraph() {
        depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public DepGraph(List<InvDep> depList) {
        this();
        depList.forEach(this::addDep);
    }

    public void addDep(InvDep invDep) {
        PlainClass inClass = invDep.getThisClass();
        PlainClass outClass = invDep.getTargetClass();

        allDepCount++;

        if (!inClass.isInPackage()) {
            return;
        }

        if (!outClass.isInPackage()) {
            return;
        }

        depGraph.addNode(inClass);
        depGraph.addNode(outClass);

        Invocation invocation = invDep.getInvocation();
        depGraph.addEdge(inClass, outClass, invocation);
    }

    public void printDependencies() {

        Set<PlainClass> classes = depGraph.nodes();
        Set<Invocation> dependencies = depGraph.edges();

        if (Settings.verbose) {
            System.out.printf("All %d classes:\n", classes.size());
            classes.forEach(class_ -> {
                if (!class_.isInPackage()) {
                    return;
                }
                System.out.println("\t" + class_.getShortName());
            });
            System.out.printf("All %d dependencies:\n", dependencies.size());
            classes.forEach(inClass -> {
                classes.forEach(outClass -> {
                    Set<Invocation> invocations = depGraph.edgesConnecting(inClass, outClass);
                    if (invocations.isEmpty()) {
                        return;
                    }
                    System.out.printf("\t%s -> %s\t(%d) {%s}\n",
                            padTo(inClass.getShortName(), 32),
                            padTo(outClass.getShortName(), 34),
                            invocations.size(),
                            String.join(", ", invocations.stream()
                                    .map(invocation -> invocation.getInvocationString())
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

    public MutableNetwork<PlainClass, Invocation> getGraph() {
        return depGraph;
    }
}

