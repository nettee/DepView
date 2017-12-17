package me.nettee.depview.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;

class JarsFinder {

    private Set<String> jarToFind;
    private Map<String, File> jarFound;

    private JarsFinder(File baseDir, List<String> jars) {
        jarToFind = new HashSet<>();
        jarFound = new HashMap<>();
        jars.forEach(jar -> jarToFind.add(jar));
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
