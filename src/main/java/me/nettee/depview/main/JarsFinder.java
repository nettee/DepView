package me.nettee.depview.main;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

// TODO test this
class JarsFinder {

    private Set<String> jarToFind;
    private Map<String, Path> jarFound;

    private JarsFinder(Path baseDir, List<String> jars) {
        jarToFind = new HashSet<>();
        jarToFind.addAll(jars);
        jarFound = new HashMap<>();
        explore(baseDir);
    }

    public static Pair<Map<String, Path>, Set<String>> find(Path baseDir, List<String> jars) {
        JarsFinder finder = new JarsFinder(baseDir, jars);
        return new ImmutablePair<>(finder.jarFound, finder.jarToFind);
    }

    private void explore(Path dir) {

        if (Files.notExists(dir)) {
            throw new IllegalStateException(String.format("Illegal directory : %s", dir.toString()));
        }

        try (Stream<Path> stream = Files.list(dir)) {
            stream.forEach(path -> {
                if (Files.isDirectory(path)) {
                    explore(path);
                } else {
                    String fileName = path.getFileName().toString();
                    if (jarToFind.contains(fileName)) {
                        jarFound.put(fileName, path);
                        jarToFind.remove(fileName);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
