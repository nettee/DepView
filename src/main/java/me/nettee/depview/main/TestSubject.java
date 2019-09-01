package me.nettee.depview.main;

import com.typesafe.config.Config;
import me.nettee.depview.utils.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestSubject {

    private static final Logger logger = LoggerFactory.getLogger(TestSubject.class);

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
        // TODO warn if same dependencies not found
        if (config.hasPath("dependency.jdk")) {
            Config jdkDependency = config.getConfig("dependency.jdk");
            Path jdkHome = Paths.get(jdkDependency.getString("home"));

            List<String> jarDependencies = jdkDependency.getStringList("jar");
            List<Path> jarsFound = FileFinder.find(jdkHome, FileFinder.byFileName(jarDependencies));
            testSubject.addJars(jarsFound);
        }

        if (config.hasPath("dependency.maven")) {
            Config mavenDependency = config.getConfig("dependency.maven");
            Path repository = Paths.get(mavenDependency.getString("repository"));

            List<String> jarDependencies = mavenDependency.getStringList("jar");

            List<Path> jarsFound = FileFinder.find(repository, FileFinder.byFileName(jarDependencies));
            testSubject.addJars(jarsFound);
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
