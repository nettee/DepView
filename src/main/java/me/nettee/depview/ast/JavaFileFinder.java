package me.nettee.depview.ast;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class JavaFileFinder {

    private List<Path> javaFiles = new ArrayList<>();

    public static JavaFileFinder find(Path dir) {
        return new JavaFileFinder(dir);
    }

    private JavaFileFinder(Path dir) {
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
                    String fileName = path.getFileName().toString();
                    if (fileName.endsWith(".java")) {
                        javaFiles.add(path);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Path> getJavaFiles() {
        return javaFiles;
    }
}
