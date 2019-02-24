package me.nettee.depview.main;

import com.typesafe.config.Config;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TestSubject {

    private String name;
    private String projectPackage;
    private List<Path> sources;
    private List<Path> classes;
    private final List<Path> jars;

    private TestSubject() {
        jars = new ArrayList<>();
    }

    static TestSubject fromConfig(Config config) {

        TestSubject testSubject = new TestSubject();
        testSubject.setName(config.getString("name"));
        testSubject.setProjectPackage(config.getString("package"));

        Config path = config.getConfig("path");
        Path projectDir = Paths.get(path.getString("base"));

        List<Path> sourcePaths = stringsToPaths(projectDir, path.getStringList("sources"));
        List<Path> classPaths = stringsToPaths(projectDir, path.getStringList("classes"));

        testSubject.setSources(sourcePaths);
        testSubject.setClasses(classPaths);

        if (config.hasPath("dependency.jar")) {
            List<Path> jars = stringsToPaths(projectDir, config.getStringList("dependency.jar"));
            testSubject.addJars(jars);
        }

        // Find jar files in file system when constructing TestSubject object.
        if (config.hasPath("dependency.jdk")) {
            Config jdkDependency = config.getConfig("dependency.jdk");
            Path jdkHome = Paths.get(jdkDependency.getString("home"));

            List<String> jarDependencies = jdkDependency.getStringList("jar");
            Pair<Map<String, Path>, Set<String>> result = JarsFinder.find(jdkHome, jarDependencies);
            Map<String, Path> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        if (config.hasPath("dependency.maven")) {
            Config mavenDependency = config.getConfig("dependency.maven");
            Path repository = Paths.get(mavenDependency.getString("repository"));

            List<String> jarDependencies = mavenDependency.getStringList("jar");
            Pair<Map<String, Path>, Set<String>> result = JarsFinder.find(repository, jarDependencies);
            Map<String, Path> jarsFound = result.getLeft();
            Set<String> jarsNotFound = result.getRight();

            if (!jarsNotFound.isEmpty()) {
                System.out.print("Warning: jars not found: ");
                jarsNotFound.forEach(fileName -> System.out.println("\t" + fileName));
            }
            testSubject.addJars(jarsFound.values());
        }

        return testSubject;
    }

    private static List<Path> stringsToPaths(Path projectDir, List<String> strings) {
        return strings.stream()
                .map(projectDir::resolve)
                .collect(Collectors.toList());
    }

    public void addJars(Collection<Path> jars) {
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

    public List<Path> getSources() {
        return sources;
    }

    public void setSources(List<Path> sources) {
        this.sources = sources;
    }

    public List<Path> getClasses() {
        return classes;
    }

    public void setClasses(List<Path> classes) {
        this.classes = classes;
    }

    public List<Path> getJars() {
        return jars;
    }
}
