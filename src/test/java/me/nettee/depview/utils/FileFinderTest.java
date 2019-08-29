package me.nettee.depview.utils;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class FileFinderTest {

    private Path dir = Paths.get("/Users/william/projects/DepView/src/main/java/me/nettee/depview/ast");

    @Test
    public void find() {
        List<Path> paths = FileFinder.find(dir, FileFinder.IS_JAVA_FILE);
        assertEquals(5, paths.size());
    }
}