package com.marklogic.spring.batch.item.file;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Unit test for verifying we construct the pattern correctly.
 */
public class MlcpFileReaderTest extends Assert {

    private MlcpFileReader sut = new MlcpFileReader();

    @Test
    public void fileDirectory() {
        assertEquals("file:src" + File.separator + "**", test("src"));
        assertEquals("file:src" + File.separator + "**", test("src" + File.separator));
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

    @Test
    public void inputFilePattern() {
        assertEquals("file:src" + File.separator + "*.java", test("src", "*.java"));
    }

    private String test(String inputFilePath) {
        return test(inputFilePath, null);
    }

    private String test(String inputFilePath, String inputFilePattern) {
        sut.setInputFilePath(inputFilePath);
        sut.setInputFilePattern(inputFilePattern);
        return sut.buildPattern();
    }
}
