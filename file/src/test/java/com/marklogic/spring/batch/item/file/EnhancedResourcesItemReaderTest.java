package com.marklogic.spring.batch.item.file;

import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;

/**
 * Unit test for verifying we construct the pattern correctly.
 */
@ContextConfiguration(classes = {EnhancedResourceItemReaderTestJobConfig.class} )
public class EnhancedResourcesItemReaderTest extends AbstractJobRunnerTest {

    private EnhancedResourcesItemReader sut = new EnhancedResourcesItemReader();

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
    public void findOneMonsterInDatabaseTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "readertest");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting 3 items in readertest collection", "readertest", 3);
    }
}
