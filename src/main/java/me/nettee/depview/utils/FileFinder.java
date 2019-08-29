package me.nettee.depview.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileFinder {

    public static final Predicate<Path> IS_JAVA_FILE = path -> {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".java");
    };

    public FileFinder() {}

    public static List<Path> find(Path dir, Predicate<Path> pattern) {
        return new FileExplorer(dir, pattern).filesFound;
    }

    private static class FileExplorer {
        private List<Path> filesFound;
        private final Predicate<Path> pattern;

        private FileExplorer(Path dir, Predicate<Path> pattern) {
            this.pattern = pattern;
            this.filesFound = new ArrayList<>();
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
                            filesFound.add(path);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
