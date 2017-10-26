package com.marklogic.spring.batch.item.reader.support;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class MapFileVisitorTest {

    @Rule
    public TemporaryFolder file = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        file.newFolder("A");
        file.newFolder("B");
        file.newFolder("A", "A1");

        file.newFile("/A/red.txt");
        file.newFile("/B/blue.jpg");
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
        assertThat("Expecting 4 files", fileVisitor.getNumberOfFiles() == 4);
        assertThat("Expecting 4 matched files", fileVisitor.getNumberOfMatches() == 4);
    }

    @Test
    public void recursiveTraverseFileTreeWithPatternTest() throws IOException {
        Path startingDir = Paths.get(file.getRoot().getAbsolutePath());
        MapFileVisitor fileVisitor = new MapFileVisitor("**/**.txt");
        Files.walkFileTree(startingDir, fileVisitor);
        Map<String, Path> fileMap = fileVisitor.getFileMap();
        System.out.println("SIZE: " + fileMap.size());
        for (Map.Entry<String, Path> item : fileMap.entrySet()) {
            System.out.println(item.getKey());
        }
        assertThat("Expecting 3 matched files", fileVisitor.getNumberOfMatches() == 3);
        assertThat("Expecting 4 files", fileVisitor.getNumberOfFiles() == 4);

    }

    @Test
    public void recursiveTraverseFileTreeWithDirectoryPatternTest() throws IOException {
        Path startingDir = Paths.get(file.getRoot().getAbsolutePath());
        MapFileVisitor fileVisitor = new MapFileVisitor("**/A/**.txt");
        Files.walkFileTree(startingDir, fileVisitor);
        Map<String, Path> fileMap = fileVisitor.getFileMap();
        System.out.println("SIZE: " + fileMap.size());
        for (Map.Entry<String, Path> item : fileMap.entrySet()) {
            System.out.println(item.getKey());
        }
        assertThat("Expecting 2 matched files", fileVisitor.getNumberOfMatches() == 2);
        assertThat("Expecting 4 files", fileVisitor.getNumberOfFiles() == 4);

    }
}
