package me.nettee.depview;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ast {

    private final ASTNode root;
    private final int kind;

    private Ast(ASTNode root, int kind) {
        this.root = root;
        this.kind = kind;
    }

    public static Ast fromFile(File file) {
        try {
            String program = readFromFile(file);
            String source = program;
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(source.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            ASTNode root = parser.createAST(null);
            return new Ast(root, ASTParser.K_COMPILATION_UNIT);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static String readFromFile(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
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
