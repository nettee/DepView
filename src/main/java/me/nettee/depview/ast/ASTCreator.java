package me.nettee.depview.ast;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ASTCreator implements Iterator<FileAst> {

    private String[] classpathEntries;
    private String[] sourcepathEntries;

    private List<String> filepaths;
    private Iterator<String> iter;

    public ASTCreator(String projectDirPath) {
        PathExplorer explorer = PathExplorer.startExplore(projectDirPath);
        classpathEntries = explorer.getClassPaths();
        sourcepathEntries = explorer.getSourcePaths();
        filepaths = explorer.getFilePaths();
        iter = filepaths.iterator();
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public FileAst next() {
        String filepath = iter.next();
        ASTNode root = createAST(filepath);
        return new FileAst(root);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private ASTNode createAST(String filepath) {

        String program;

        try {
            program = readFromFile(filepath);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read from file " + filepath);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS3);

        parser.setSource(program.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
        parser.setUnitName(filepath);
        parser.setResolveBindings(true);

        return parser.createAST(null);
    }

    private String readFromFile(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            sb.append(line);
            sb.append(ls);
        }
        in.close();
        return sb.toString();
    }

}