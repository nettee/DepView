package me.nettee.depview.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class JavaFileFinder {

    private List<Path> javaFiles;
    private final Predicate<Path> pattern;

    private static Predicate<Path> isJavaFile = path -> {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".java");
    };

    public static List<Path> find(Path dir, Predicate<Path> pattern) {
        return new JavaFileFinder(dir, pattern).javaFiles;
    }

    public static List<Path> find(Path dir) {
        return find(dir, isJavaFile);
    }

    private JavaFileFinder(Path dir, Predicate<Path> pattern) {
        this.pattern = pattern;
        this.javaFiles = new ArrayList<>();
        explore(dir);
    }

    // Recursively explore all files under dir
    private void explore(Path dir) {

        if (Files.notExists(dir)) {
            throw new IllegalStateException("Illegal Directory Path: " + dir.toString());
        }

        try (Stream<Path> stream = Files.list(dir)) {
            stream.forEach(path -> {
                if (Files.isDirectory(path)) {
                    explore(path);
                } else {
                    if (pattern.test(path)) {
                        javaFiles.add(path);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
