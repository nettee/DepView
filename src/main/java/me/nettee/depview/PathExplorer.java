package me.nettee.depview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class PathExplorer {

    private List<String> filePaths = new ArrayList<String>();
    private List<String> srcs = new ArrayList<String>();
    private List<String> bins = new ArrayList<String>();

    public static PathExplorer startExplore(String dirPath) {
        return new PathExplorer(dirPath);
    }

    private PathExplorer(String dirPath) {
        readDirectory(dirPath);
    }

    // recursively read all files under dirPath
    private void readDirectory(String dirPath) {

        File dir = new File(dirPath);

        if (!dir.exists()) {
            throw new IllegalStateException("Illegal Directory Path: " + dir.getAbsolutePath());
        }

        for (File subdir : dir.listFiles()) {
            if (subdir.isDirectory()) {
                if (subdir.getName().trim().equals("bin")) {
                    bins.add(subdir.getAbsolutePath());
                } else if (subdir.getName().startsWith("src")) {
                    srcs.add(subdir.getAbsolutePath());
                }
                // recursively read subdirectories except bin/
                if (!subdir.getName().startsWith("bin")) {
                    readDirectory(subdir.getPath());
                }
            } else {
                String fileName = subdir.getName();
                if (fileName.endsWith(".java") && !fileName.contains("Test")) {
                    filePaths.add(subdir.getAbsolutePath());
                } else if (fileName.endsWith(".jar")) {
                    bins.add(subdir.getAbsolutePath());
                }
            }
        }
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public String[] getSourcePaths() {
        return srcs.toArray(new String[srcs.size()]);
    }

    public String[] getClassPaths() {
        return bins.toArray(new String[bins.size()]);
    }
}
