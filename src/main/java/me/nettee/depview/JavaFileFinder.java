package me.nettee.depview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * get:
 * <li>all java files' filenames and paths</li>
 * <li>all the source directories's paths</li>
 * <li>all the binary files' paths</li> in a project
 *
 */
public class JavaFileFinder {

    private List<File> filePaths = new ArrayList<File>();

    public static JavaFileFinder find(File dir) {
        return new JavaFileFinder(dir);
    }

    private JavaFileFinder(File dir) {
        readDirectory(dir);
    }

    // recursively read all files under dirPath
    private void readDirectory(File dir) {

        if (!dir.exists()) {
            throw new IllegalStateException("Illegal Directory Path: " + dir.getAbsolutePath());
        }

        for (File subdir : dir.listFiles()) {
            if (subdir.isDirectory()) {
                // recursively read subdirectories
                readDirectory(subdir);
            } else {
                if (subdir.getName().endsWith(".java")) {
                    filePaths.add(subdir);
                }
            }
        }
    }

    public List<File> getFiles() {
        return filePaths;
    }
}