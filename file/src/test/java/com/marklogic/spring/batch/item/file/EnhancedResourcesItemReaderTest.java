package com.marklogic.spring.batch.item.file;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for verifying we construct the pattern correctly.
 */
public class EnhancedResourcesItemReaderTest extends Assert {

    private EnhancedResourcesItemReader sut = new EnhancedResourcesItemReader();

    @Rule
    public TemporaryFolder file = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        file.newFolder("A");
        file.newFolder("B");
        file.newFolder("A", "A1");

        file.newFile("/A/red.txt");
        file.newFile("/B/blue.jpg");
        file.newFile("/B/yellow.txt");
        file.newFile("/A/A1/green.txt");
    }

    @Test
    public void fileDirectory() {
        assertEquals("file:src/**", test("src"));
        assertEquals("file:src" + File.separator + "/**", test("src" + File.separator));
    }

    @Test
    public void specificFile() {
        assertEquals("file:build.gradle", test("build.gradle"));
        assertEquals("file:./build.gradle", test("./build.gradle"));
    }

    @Test
    public void classpathReference() {
        assertEquals("classpath:org/example", test("classpath:org/example"));
        assertEquals("classpath:org/example/", test("classpath:org/example/"));
    }

    @Test
    public void withFilePrefix() {
        assertEquals("If the client includes file:, then a pattern won't be included automatically",
                "file:src", test("file:src"));
    }

    private String test(String inputFilePath) {
        sut.setInputFilePath(inputFilePath);
        return sut.buildPattern();
    }

    @Test
    public void recursiveTest() {
        sut.setInputFilePath(file.getRoot().getAbsolutePath());
        sut.setInputFilePattern(".*\\.txt");

        ExecutionContext context = new ExecutionContext();
        sut.open(context);

        try {
            List<Resource> resourceList = new ArrayList<>();
            Resource resource = null;
            while ( (resource = sut.read()) != null ) {
                resourceList.add(resource);
            }

            assertEquals("Expecting 3 .txt files", resourceList.size(), 3);
        } catch (Exception e) {
            fail("Cannot get resource list from EnhancedResourcesItemReader: " + e);
            e.printStackTrace();
        }
    }
}
