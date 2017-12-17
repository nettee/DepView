package me.nettee.depview.ast;

import me.nettee.depview.model.JavaFileFinder;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ASTCreator implements Iterator<FileAst> {

    private String[] classpathEntries;
    private String[] sourcepathEntries;

    private List<File> javaFiles;
    private Iterator<File> iter;

    private String[] fileListToStringArray(List<File> fileList) {
        return fileList.stream()
                .map(file -> file.getAbsolutePath())
                .collect(Collectors.toList())
                .toArray(new String[fileList.size()]);
    }

    public ASTCreator(List<File> sourcePaths, List<File> classPaths) {
        sourcepathEntries = fileListToStringArray(sourcePaths);
        classpathEntries = fileListToStringArray(classPaths);

        System.out.println("classpath entries:");
        classPaths.forEach(path -> System.out.println("\t" + path));

        javaFiles = new ArrayList<>();
        for (File sourcePath : sourcePaths) {
            JavaFileFinder finder = JavaFileFinder.find(sourcePath);
            javaFiles.addAll(finder.getJavaFiles());
        }
        System.out.printf("Found %d java files:\n", javaFiles.size());
        for (File filepath : javaFiles) {
            System.out.println("\t" + filepath.getPath());
        }
        iter = javaFiles.iterator();
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public FileAst next() {
        File file = iter.next();
        ASTNode root = createAST(file);
        return new FileAst(root);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private ASTNode createAST(File file) {

        String filepath = file.getAbsolutePath();
        String program;

        try {
            program = readFromFile(filepath);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read from file " + filepath);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS4);

        parser.setSource(program.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
//        parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
//        parser.setEnvironment(null, null, null, true);
        parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
        parser.setUnitName(filepath);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

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