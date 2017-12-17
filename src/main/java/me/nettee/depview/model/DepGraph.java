package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class DepGraph {

    private MutableNetwork<PlainClass, Invocation> depGraph;

    public DepGraph() {
        depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public void addDep(InvDep invDep) {
        PlainClass inClass = invDep.getThisClass();
        PlainClass outClass = invDep.getTargetClass();
        Invocation invocation = invDep.getInvocation();
        depGraph.addNode(inClass);
        depGraph.addNode(outClass);
        depGraph.addEdge(inClass, outClass, invocation);
    }

    public void showDependencies() {

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
}
