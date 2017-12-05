package me.nettee.depview;

import java.io.File;

public class DepView {

    private final File projectPath;
    private final File[] sourcePaths;

    public DepView(File projectPath, File[] sourcePaths) {
        this.projectPath = projectPath;
        this.sourcePaths = sourcePaths;
    }

    public void view() {
        System.out.printf("Project path: %s\n", projectPath.getPath());
        for (File sourcePath : sourcePaths) {
            System.out.printf("Source path: %s\n", sourcePath.getPath());
        }
    }

    public static void main(String[] args) {
        File projectPath = new File("/home/william/projects/aql/aql-client");
        File sourcePath = new File(projectPath, "src");
        DepView depView = new DepView(projectPath, new File[]{sourcePath});
        depView.view();
    }
}
