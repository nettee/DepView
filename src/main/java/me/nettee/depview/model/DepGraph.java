package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class DepGraph {

    private final String package_;
    private final MutableNetwork<PlainClass, Invocation> depGraph;

    public DepGraph(String package_) {
        this.package_ = package_;
        depGraph = NetworkBuilder.directed()
                .allowsParallelEdges(true)
                .allowsSelfLoops(true)
                .build();
    }

    public void addDep(InvDep invDep) {
        PlainClass inClass = invDep.getThisClass();
        PlainClass outClass = invDep.getTargetClass();

        if (!outClass.isInPackage(package_)) {
            return;
        }

        depGraph.addNode(inClass);
        depGraph.addNode(outClass);

        Invocation invocation = invDep.getInvocation();
        depGraph.addEdge(inClass, outClass, invocation);
    }

    public void showDependencies() {

        Set<PlainClass> classes = depGraph.nodes();
        System.out.printf("All %d classes:\n", classes.size());
        classes.forEach(class_ -> {
            if (!class_.isInPackage(package_)) {
                return;
            }
            System.out.println("\t" + class_.getShortName(package_));
        });

        Set<Invocation> dependencies = depGraph.edges();
        System.out.printf("All %d dependencies:\n", dependencies.size());
        classes.forEach(inClass -> {
            classes.forEach(outClass -> {
                Set<Invocation> invocations = depGraph.edgesConnecting(inClass, outClass);
                if (invocations.isEmpty()) {
                    return;
                }
                System.out.printf("\t%s -> %s\t(%d) {%s}\n",
                        padTo(inClass.getShortName(package_), 32),
                        padTo(outClass.getShortName(package_), 34),
                        invocations.size(),
                        String.join(", ", invocations.stream()
                                .map(invocation -> invocation.getInvocationString())
                                .collect(Collectors.toList())));
            });
        });
    }

    private static String padTo(String s, int len) {
        if (s.length() >= len) {
            return s;
        } else {
            return s + StringUtils.repeat(' ', len - s.length());
        }
    }
}
