package me.nettee.depview.utils;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class JavaFileFinderTest {

    private Path dir = Paths.get("/Users/william/projects/DepView/src/main/java/me/nettee/depview/ast");


    @Test
    public void find() {
        List<Path> paths = JavaFileFinder.find(dir, path -> path.getFileName().toString().endsWith(".java"));
        assertEquals(5, paths.size());
    }
}