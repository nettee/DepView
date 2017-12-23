package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;

import java.util.*;

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

        Set<String> packages = new HashSet<>();
        classes.forEach(class_ -> {
            String package_ = class_.getPackage();
            packages.add(package_);
        });

        Map<String, Integer> packageGroups = new HashMap<>();
        {
            int group = 0;
            for (String package_ : packages) {
                packageGroups.put(package_, group);
                group++;
            }
        }

        classes.forEach(class_ -> {
            int group = packageGroups.get(class_.getPackage());
            Node node = new Node(class_.getName(), group);
            nodes.add(node);
        });

        classes.forEach(inClass -> {
            classes.forEach(outClass -> {
                Set<Invocation> invocations = depGraph.edgesConnecting(inClass, outClass);
//                int factor = inClass.isSamePackageWith(outClass) ? 3 : 1;
//                int count = invocations.size() > 3 ? invocations.size() : 0;
//                int value = factor * count;
                int value = invocations.size();
                if (value > 0) {
                    Link link = new Link(inClass.getName(), outClass.getName(), value);
                    links.add(link);
                }
            });
        });
    }

    public static D3Graph fromDepGraph(DepGraph depGraph) {
        MutableNetwork<PlainClass, Invocation> graph = depGraph.getGraph();
        return new D3Graph(graph);
    }
}

