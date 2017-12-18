package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class D3Graph {

    class Node {
        private String id;
        private int group;

        Node(String id, int group) {
            this.id = id;
            this.group = group;
        }
    }

    class Link {
        private String source;
        private String target;
        private int value;

        public Link(String source, String target, int value) {
            this.source = source;
            this.target = target;
            this.value = value;
        }
    }

    private List<Node> nodes = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    private D3Graph(MutableNetwork<PlainClass, Invocation> depGraph) {

        Set<PlainClass> classes = depGraph.nodes();
        classes.forEach(class_ -> {
            Node node = new Node(class_.getName(), 0);
            nodes.add(node);
        });

        classes.forEach(inClass -> {
            classes.forEach(outClass -> {
                Set<Invocation> invocations = depGraph.edgesConnecting(inClass, outClass);
                Link link = new Link(inClass.getName(), outClass.getName(), invocations.size());
                links.add(link);
            });
        });
    }

    public static D3Graph fromDepGraph(DepGraph depGraph) {
        MutableNetwork<PlainClass, Invocation> graph = depGraph.getGraph();
        return new D3Graph(graph);
    }
}

