package me.nettee.depview.ast;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;

public class JarsFinder {

    private Set<String> jarToFind;
    private Map<String, File> jarFound;

    private JarsFinder(File baseDir, List<String> jars) {
        jarToFind = new HashSet<>();
        jarFound = new HashMap<>();
        jars.forEach(jar -> jarToFind.add(jar));
        System.out.println("Jar to find: \n\t" + String.join("\n\t", jarToFind));
        explore(baseDir);
    }

    public static Pair<Map<String, File>, Set<String>> find(File baseDir, List<String> jars) {
        JarsFinder finder = new JarsFinder(baseDir, jars);
        Map<String, File> jarFound = finder.jarFound;
        Set<String> jarNotFound = finder.jarToFind;
        Pair<Map<String, File>, Set<String>> result = new ImmutablePair<>(jarFound, jarNotFound);
        return result;
    }

    private void explore(File dir) {

        if (!dir.exists()) {
            throw new IllegalStateException("Illegal directory : " + dir.getAbsolutePath());
        }

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                explore(file);
            } else {
                String fileName = file.getName();
                if (jarToFind.contains(fileName)) {
                    jarFound.put(fileName, file);
                    jarToFind.remove(fileName);
                }
            }
        }
    }
}
