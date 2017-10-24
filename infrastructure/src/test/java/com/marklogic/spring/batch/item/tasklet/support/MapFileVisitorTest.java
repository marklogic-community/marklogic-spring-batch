package com.marklogic.spring.batch.item.tasklet.support;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class MapFileVisitorTest {

    @Rule
    public TemporaryFolder file = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        file.newFolder("A");
        file.newFolder("B");
        file.newFolder("A", "A1");

        file.newFile("/A/red.txt");
        file.newFile("/B/blue.txt");
        file.newFile("/A/A1/green.txt");
        file.newFile("/B/yellow.txt");
    }

    @Test
    public void recursiveTraverseFileTreeTest() throws IOException {
        Path startingDir = Paths.get(file.getRoot().getAbsolutePath());
        MapFileVisitor fileVisitor = new MapFileVisitor();
        Files.walkFileTree(startingDir, fileVisitor);
        Map<String, Path> fileMap = fileVisitor.getFileMap();
        System.out.println("SIZE: " + fileMap.size());
        for (Map.Entry<String, Path> item : fileMap.entrySet()) {
            System.out.println(item.getKey());
        }
    }
}
