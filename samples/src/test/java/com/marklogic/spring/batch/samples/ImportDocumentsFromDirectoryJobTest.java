package com.marklogic.spring.batch.samples;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {ImportDocumentsFromDirectoryJobConfig.class})
public class ImportDocumentsFromDirectoryJobTest extends AbstractJobRunnerTest {

    private JobParametersBuilder jpb = new JobParametersBuilder();

    @Test
    public void loadXmlDocumentsTest() throws Exception {
        jpb.addString("input_file_path", "src/test/resources/data/*.xml");
        jpb.addString("document_type", "xml");
        jpb.addString("input_file_pattern", "(elmo|grover).xml");
        jpb.addString("output_collections", "monster,sesameStreet");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadXmlDocumentsWithoutCollectionsTest() throws Exception {
        jpb.addString("input_file_path", "src/test/resources/data/*.xml");
        jpb.addString("document_type", "xml");
        jpb.addString("input_file_pattern", "(elmo|grover).xml");
        jpb.addString("output_uri_replace", ".*data/,/");
        jpb.addString("output_uri_prefix", "/monster");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        Fragment frag = getClientTestHelper().parseUri("/monster/elmo.xml");
        frag.assertElementValue("/monster/name", "Elmo");
        thenDocumentsInMonsterCollection(0);
    }

    @Test
    public void loadJsonDocumentsTest() throws Exception {
        jpb.addString("input_file_path", "src/test/resources/data/*.json");
        jpb.addString("input_file_pattern", "(elmo|grover).json");
        jpb.addString("document_type", "json");
        jpb.addString("output_collections", "monster,sesameStreet");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        thenDocumentsInMonsterCollection(2);
    }
/*
    @Test
    public void loadXmlJsonAndTextDocumentsTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.*",
                "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(8);
    }

    @Test
    public void loadDocumentsAndTransformUriTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.*",
                "--output_collections", "monster,sesameStreet",
                "--output_uri_replace", ".*data/,/",
                "--output_uri_prefix", "/monster");
        thenDocumentsInMonsterCollection(8);
        Fragment frag = client.parseUri("/monster/bigbird.xml", "monster", "sesameStreet");
        frag.assertElementValue("/monster/name", "BigBird");
    }

    @Test
    public void loadBinaryDocumentsTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/binary/*.*",
                "--document_type", "binary",
                "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadTextDocumentsTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.txt",
                "--document_type", "text",
                "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test(expected=AssertionError.class)
    public void inputFilePathExceptionTest() {

        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--document_type", "text",
                "--output_collections", "monster,sesameStreet");
    }
*/
    public void thenDocumentsInMonsterCollection(int expectedCount) {
        getClientTestHelper().assertCollectionSize("Expect 2 docs in monster collection", "monster", expectedCount);
        getClientTestHelper().assertCollectionSize("Expect 2 docs in sesame street collection", "sesameStreet", expectedCount);
    }

}
