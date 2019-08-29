package me.nettee.depview.ast;

import com.google.common.collect.Streams;
import me.nettee.depview.main.Env;
import me.nettee.depview.main.Printer;
import me.nettee.depview.utils.FileFinder;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTCreator {

    private static final Logger logger = LoggerFactory.getLogger(ASTCreator.class);

    private final String[] classpathEntries;
    private final String[] sourcepathEntries;
    private final Env env;

    private List<Path> javaFiles;
    private Iterator<Path> iter;

    private String[] pathListToStringArray(List<Path> pathList) {
        return pathList.stream()
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .collect(Collectors.toList())
                .toArray(new String[pathList.size()]);
    }

    public ASTCreator(Env env, List<Path> sourcePaths, List<Path> classPaths) {
        this.env = env;
        this.sourcepathEntries = pathListToStringArray(sourcePaths);
        this.classpathEntries = pathListToStringArray(classPaths);

        // TODO verbose 使用 logger 级别代替
        if (logger.isDebugEnabled()) {
            logger.debug(Printer.list("Classpath entries", classPaths));
        }

        javaFiles = new ArrayList<>();
        for (Path sourcePath : sourcePaths) {
            List<Path> javaFiles = FileFinder.find(sourcePath, FileFinder.IS_JAVA_FILE);
            this.javaFiles.addAll(javaFiles);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Found {} java files:\n{}", javaFiles.size(), Printer.list(javaFiles));
        }

        iter = javaFiles.iterator();
    }

    public Iterator<FileAst> iterator() {
        return new Iterator<FileAst>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public FileAst next() {
                Path path = iter.next();
                ASTNode root = createAST(path);
                return new FileAst(env, root);
            }
        };
    }

    public Stream<FileAst> stream() {
        return Streams.stream(iterator());
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