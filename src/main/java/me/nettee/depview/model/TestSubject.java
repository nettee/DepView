package me.nettee.depview.model;

import com.typesafe.config.Config;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TestSubject {

    private String name;
    private String projectPackage;
    private List<File> sources;
    private List<File> classes;
    private final List<File> jars;

    public TestSubject() {
        jars = new ArrayList<>();
    }

    public static TestSubject fromConfig(Config config) {

        TestSubject testSubject = new TestSubject();
        testSubject.setName(config.getString("name"));
        testSubject.setProjectPackage(config.getString("package"));

        Config path = config.getConfig("path");
        File projectDir = new File(path.getString("base"));

        List<File> sourcePaths = path.getStringList("sources").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());
        List<File> classPaths = path.getStringList("classes").stream()
                .map(pathName -> new File(projectDir, pathName))
                .collect(Collectors.toList());

        testSubject.setSources(sourcePaths);
        testSubject.setClasses(classPaths);

        if (config.hasPath("dependency.jar")) {
            List<File> jars = config.getStringList("dependency.jar").stream()
                    .map(jarDependency -> new File(projectDir, jarDependency))
                    .collect(Collectors.toList());
            testSubject.addJars(jars);
        }

        if (config.hasPath("dependency.jdk")) {
            Config jdkDependency = config.getConfig("dependency.jdk");
            File jdkHome = new File(jdkDependency.getString("home"));

            List<String> jarDependencies = jdkDependency.getStringList("jar");
            Pair<Map<String, File>, Set<String>> result = JarsFinder.find(jdkHome, jarDependencies);
            Map<String, File> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        if (config.hasPath("dependency.maven")) {
            Config mavenDependency = config.getConfig("dependency.maven");
            File repository = new File(mavenDependency.getString("repository"));

            List<String> jarDependencies = mavenDependency.getStringList("jar");
            Pair<Map<String, File>, Set<String>> result = JarsFinder.find(repository, jarDependencies);
            Map<String, File> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        return testSubject;
    }

    public void addJars(Collection<File> jars) {
        this.jars.addAll(jars);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectPackage() {
        return projectPackage;
    }

    public void setProjectPackage(String projectPackage) {
        this.projectPackage = projectPackage;
    }

    public List<File> getSources() {
        return sources;
    }

    public void setSources(List<File> sources) {
        this.sources = sources;
    }

    public List<File> getClasses() {
        return classes;
    }

    public void setClasses(List<File> classes) {
        this.classes = classes;
    }

    public List<File> getJars() {
        return jars;
    }
}
