package me.nettee.depview.ast;

import me.nettee.depview.main.Settings;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ASTCreator implements Iterator<FileAst> {

    private final String[] classpathEntries;
    private final String[] sourcepathEntries;
    private final String projectPackage;

    private List<Path> javaFiles;
    private Iterator<Path> iter;

    private String[] pathListToStringArray(List<Path> pathList) {
        return pathList.stream()
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .collect(Collectors.toList())
                .toArray(new String[pathList.size()]);
    }

    public ASTCreator(List<Path> sourcePaths, List<Path> classPaths, String projectPackage) {
        this.sourcepathEntries = pathListToStringArray(sourcePaths);
        this.classpathEntries = pathListToStringArray(classPaths);
        this.projectPackage = projectPackage;

        if (Settings.verbose) {
            System.out.println("classpath entries:");
            classPaths.forEach(path -> System.out.println("\t" + path));
        }

        javaFiles = new ArrayList<>();
        for (Path sourcePath : sourcePaths) {
            JavaFileFinder finder = JavaFileFinder.find(sourcePath);
            javaFiles.addAll(finder.getJavaFiles());
        }

        if (Settings.verbose) {
            System.out.printf("Found %d java files:\n", javaFiles.size());
            for (Path filepath : javaFiles) {
                System.out.println("\t" + filepath.toString());
            }
        }

        iter = javaFiles.iterator();
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public FileAst next() {
        Path path = iter.next();
        ASTNode root = createAST(path);
        return new FileAst(root, projectPackage);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private ASTNode createAST(Path path) {

        String pathString = path.toAbsolutePath().toString();
        String program;

        try {
            program = readFromFile(path);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read from file " + pathString);
        }

        ASTParser parser = ASTParser.newParser(AST.JLS4);

        parser.setSource(program.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
//        parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
//        parser.setEnvironment(null, null, null, true);
        parser.setEnvironment(classpathEntries, sourcepathEntries, null, true);
        parser.setUnitName(pathString);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

        return parser.createAST(null);
    }

    private String readFromFile(Path path) throws IOException {
        String ls = System.getProperty("line.separator");
        List<String> lines = Files.readAllLines(path);
        return String.join(ls, lines);
    }

}