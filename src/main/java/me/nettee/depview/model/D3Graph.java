package me.nettee.depview.model;

import com.google.common.graph.MutableNetwork;

import java.util.*;
import java.util.function.Function;

// This class is for GSON serialization
public class D3Graph {

    class Node {
        private String id;
        private int radius;
        private int group;

        Node(String id, int radius, int group) {
            this.id = id;
            this.radius = radius;
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

    private D3Graph(MutableNetwork<PlainClass, DepAttr> depGraph,
                    Map<PlainClass, Integer> classSizes) {

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

        // y = k log x + b
        final int maxRadius = 16;
        final int minRadius = 8;
        final int maxSize = classSizes.values().stream().max(Integer::compareTo).get();
        final int minSize = classSizes.values().stream().min(Integer::compareTo).get();
        final double k = (double) (maxRadius - minRadius)
                / (Math.log(maxSize) - Math.log(minSize));
        final double b = minRadius - k * Math.log(minSize);
        Function<Integer, Integer> rad = size -> (int) (k * Math.log(size) + b);

        classes.forEach(class_ -> {
            int radius = rad.apply(classSizes.getOrDefault(class_, minSize));
            int group = packageGroups.get(class_.getPackage());
            Node node = new Node(class_.getName(), radius, group);
            nodes.add(node);
        });

        classes.forEach(inClass -> {
            classes.forEach(outClass -> {
                Set<DepAttr> depAttrs = depGraph.edgesConnecting(inClass, outClass);
//                int factor = inClass.isSamePackageWith(outClass) ? 3 : 1;
//                int count = invocations.size() > 3 ? invocations.size() : 0;
//                int value = factor * count;
                int value = depAttrs.size();
                if (value > 0) {
                    // Do NOT create links for whose value is zero!
                    Link link = new Link(inClass.getName(), outClass.getName(), value);
                    links.add(link);
                }
            });
        });
    }

    public static D3Graph fromDepGraph(DepGraph depGraph) {
        return new D3Graph(depGraph.getGraph(), depGraph.getClassSizes());
    }
}

