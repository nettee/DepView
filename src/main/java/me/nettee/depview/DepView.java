package me.nettee.depview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DepView {

    private final File projectPath;

    private File[] sourcePaths;
    private String sourceFileExcludingRegex;

    public DepView(File projectPath) {
        this.projectPath = projectPath;
    }

    public void setSourcePaths(File[] sourcePaths) {
        this.sourcePaths = sourcePaths;
    }

    public void setSourceFileExcludingRegex(String sourceFileExcludingRegex) {
        this.sourceFileExcludingRegex = sourceFileExcludingRegex;
    }

    public void view() {
        ASTCreator astCreator = new ASTCreator(projectPath.getPath());

        while (astCreator.hasNext()) {
            Ast ast = astCreator.next();
            ast.visitWith(new InvocationVisitor());
            break;
        }

        System.out.println("Done.");
    }

    public static void main(String[] args) {
        File projectPath = new File("/home/william/projects/aql/aql-client");
        File sourcePath = new File(projectPath, "src");
        DepView depView = new DepView(projectPath);
        depView.setSourcePaths(new File[]{sourcePath});
        depView.setSourceFileExcludingRegex("Test");
        depView.view();
    }
}
