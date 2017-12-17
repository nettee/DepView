package me.nettee.depview.main;

import static com.google.common.base.Preconditions.checkNotNull;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import me.nettee.depview.ast.ASTCreator;
import me.nettee.depview.ast.ClassAst;
import me.nettee.depview.ast.FileAst;
import me.nettee.depview.ast.InvocationVisitor;
import me.nettee.depview.model.*;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class DepView {

    private final TestSubject testSubject;

    public DepView(TestSubject testSubject) {
        this.testSubject = testSubject;
    }

    public void view() {

        System.out.println("Test subject: " + testSubject.getName());
        String package_ = testSubject.getPackage();
        System.out.println("Package name: " + package_);

        List<File> sources = testSubject.getSources();
        List<File> classes = testSubject.getClasses();
        List<File> jars = testSubject.getJars();

        checkNotNull(sources);
        checkNotNull(classes);

        Consumer<File> filePathPrinter = file -> System.out.println("\t" + file.getPath());
        System.out.println("Sources:");
        sources.forEach(filePathPrinter);
        System.out.println("Classes:");
        classes.forEach(filePathPrinter);
        System.out.println("Jars:");
        jars.forEach(filePathPrinter);

//        classes.addAll(jars);

        ASTCreator astCreator = new ASTCreator(sources, jars);

        DepGraph depGraph = new DepGraph(package_);

        while (astCreator.hasNext()) {
            FileAst fileAst = astCreator.next();
            Iterable<ClassAst> classAsts = fileAst.getClassAsts();

            for (ClassAst classAst : classAsts) {
                InvocationVisitor visitor = new InvocationVisitor(classAst.getPlainClass(), package_);

                classAst.visitWith(visitor);

                List<InvDep> invDeps = visitor.getInvDeps();
                invDeps.forEach(invDep -> {
                    depGraph.addDep(invDep);
                });
            }
        }

        depGraph.showDependencies();

        System.out.println("Done.");
    }

    public static void main(String[] args) {

//        String filename = "astcomparator.conf";
        String filename = "aql-client.conf";

        File testSubjectConfigFile = new File(filename);

        Config conf = ConfigFactory.parseFile(testSubjectConfigFile);
        Config config = conf.getConfig("testSubject");

        TestSubject testSubject = TestSubject.fromConfig(config);

        DepView depView = new DepView(testSubject);
        depView.view();
    }

}
