package me.nettee.depview.main;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Printer {

    private Printer() {}

    public static String list(String name, List<?> l) {
        return name + ":\n" + list(l);
    }

    public static String list(List<?> l) {
        return list(l.stream());
    }

    public static String list(Stream<?> stream) {
        return stream.map(Object::toString)
                .map(s -> "\t" + s)
                .collect(Collectors.joining("\n"));
    }
}
