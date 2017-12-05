package me.nettee.depview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * get:
 * <li>all java files' filenames and paths</li>
 * <li>all the source directories's paths</li>
 * <li>all the binary files' paths</li> in a project
 *
 */
public class JavaFileFinder {

    private final Pattern excludingPattern; // Null if no excluding pattern

    private List<File> filePaths = new ArrayList<File>();

    public static JavaFileFinder find(File dir, String fileExcludingRegex) {
        Pattern pattern = Pattern.compile(fileExcludingRegex);
        return new JavaFileFinder(dir, pattern);
    }

    public static JavaFileFinder find(File dir) {
        return new JavaFileFinder(dir, null);
    }

    private JavaFileFinder(File dir, Pattern excludingPattern) {
        this.excludingPattern = excludingPattern;
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
                // subdir represents a file
                String fileName = subdir.getName();
                if (isWantedFile(fileName)) {
                    filePaths.add(subdir);
                }
            }
        }
    }

    private boolean isWantedFile(String fileName) {
        if (!fileName.endsWith(".java")) {
            return false;
        }
        if (excludingPattern == null) {
            return true;
        }
        Matcher matcher = excludingPattern.matcher(fileName);
        boolean found = matcher.find();
        return !found;
    }

    public List<File> getFiles() {
        return filePaths;
    }
}