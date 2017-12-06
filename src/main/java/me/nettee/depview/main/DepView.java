package me.nettee.depview.main;

import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;

import java.io.File;

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
            FileAst ast = astCreator.next();
            InvocationVisitor visitor = new InvocationVisitor();
            ast.visitWith(visitor);
            visitor.printInvocations();
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
