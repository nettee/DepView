package me.nettee.depview.main;

import com.typesafe.config.Config;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.nettee.depview.utils.FileFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestSubject {

    private static final Logger logger = LoggerFactory.getLogger(TestSubject.class);

    /**
     * The name of test subject.
     */
    @Getter
    @Setter
    private String name;

    /**
     * Java package name of the project.
     *
     * <p>Only classes within this package will be analyzed.
     */
    @Getter
    @Setter
    private String projectPackage;

    /**
     * Paths of Java files.
     *
     * <p>e.g. {@code src/main/java} for Maven/Gradle projects.
     */
    @Getter
    @Setter
    private List<Path> sources;

    /**
     * Paths of bytecode files
     *
     * <p>e.g. {@code target/classes} for Maven projects, {@code build/classes} for Gradle projects.
     */
    @Getter
    @Setter
    private List<Path> classes;

    @Getter
    @Setter
    private List<Path> jars = new ArrayList<>();

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

        List<Path> allJars = new ArrayList<>();

        if (config.hasPath("dependency.jar")) {
            List<Path> jars = stringsToPaths(projectDir, config.getStringList("dependency.jar"));
            allJars.addAll(jars);
        }

        // Find jar files in file system when constructing TestSubject object.
        // TODO warn if same dependencies not found
        if (config.hasPath("dependency.jdk")) {
            Config jdkDependency = config.getConfig("dependency.jdk");
            Path jdkHome = Paths.get(jdkDependency.getString("home"));

            List<String> jarDependencies = jdkDependency.getStringList("jar");
            List<Path> jarsFound = FileFinder.find(jdkHome, FileFinder.byFileName(jarDependencies));
            allJars.addAll(jarsFound);
        }

        if (config.hasPath("dependency.maven")) {
            Config mavenDependency = config.getConfig("dependency.maven");
            Path repository = Paths.get(mavenDependency.getString("repository"));

            List<String> jarDependencies = mavenDependency.getStringList("jar");

            List<Path> jarsFound = FileFinder.find(repository, FileFinder.byFileName(jarDependencies));
            allJars.addAll(jarsFound);
        }

        testSubject.setJars(allJars);

        return testSubject;
    }

    private static List<Path> stringsToPaths(Path projectDir, List<String> strings) {
        return strings.stream()
                .map(projectDir::resolve)
                .collect(Collectors.toList());
    }
}