package me.nettee.depview.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaFileFinder {

    private List<File> javaFiles = new ArrayList<>();

    public static JavaFileFinder find(File dir) {
        return new JavaFileFinder(dir);
    }

    private JavaFileFinder(File dir) {
        explore(dir);
    }

    // Recursively explore all files under dirPath
    private void explore(File dir) {

        if (!dir.exists()) {
            throw new IllegalStateException("Illegal Directory Path: " + dir.getAbsolutePath());
        }

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                explore(file);
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
    }

    public List<File> getJavaFiles() {
        return javaFiles;
    }
}
