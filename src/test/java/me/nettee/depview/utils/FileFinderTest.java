package me.nettee.depview.utils;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class FileFinderTest {

    @Test
    public void find_javaFile() {
        Path dir = Paths.get("/Users/william/projects/DepView/src/main/java/me/nettee/depview/ast");

        List<Path> paths = FileFinder.find(dir, FileFinder.IS_JAVA_FILE);
        assertEquals(5, paths.size());
    }

    @Test
    public void find_jarDependencies() {
        Path jdkHome = Paths.get("/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home");
        String[] jdkJarDependencies = {"rt.jar", "dt.jar", "tools.jar"};

        List<Path> jarsFound = FileFinder.find(jdkHome, FileFinder.byFileName(jdkJarDependencies));
        assertThat(jarsFound).hasSameSizeAs(jdkJarDependencies);
    }

    @Test
    public void find_mavenDependencies() {
        Path mavenRepository = Paths.get("/Users/william/.m2/repository");
        String[] mavenJarDependencies = {
                "guava-23.0.jar",
                "commons-lang3-3.1.jar",
                "maven-core-3.0.jar",
        };

        List<Path> jarsFound = FileFinder.find(mavenRepository, FileFinder.byFileName(mavenJarDependencies));
        assertThat(jarsFound).hasSameSizeAs(mavenJarDependencies);
    }
}