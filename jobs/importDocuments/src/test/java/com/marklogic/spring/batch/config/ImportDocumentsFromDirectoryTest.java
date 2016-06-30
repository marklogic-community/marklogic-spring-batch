package com.marklogic.spring.batch.config;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {JobProjectTestConfig.class})
public class ImportDocumentsFromDirectoryTest extends AbstractJobTest {

    ClientTestHelper client;

    @Before
    public void setup() {
        client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
    }

    @Test
    public void loadXmlDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.xml",
                "--input_file_pattern", "(elmo|grover).xml",
                "--document_type", "xml",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadXmlDocumentsWithoutCollectionsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.xml",
                "--input_file_pattern", "(elmo|grover).xml",
                "--output_uri_replace", ".*data/,/",
                "--output_uri_prefix", "/monster");
        Fragment frag = client.parseUri("/monster/elmo.xml");
        frag.assertElementValue("/monster/name", "Elmo");
        thenDocumentsInMonsterCollection(0);
    }

    @Test
    public void loadJsonDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.json",
                "--input_file_pattern", "(elmo|grover).json",
                "--document_type", "json",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadXmlJsonAndTextDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.*",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(8);
    }

    @Test
    public void loadDocumentsAndTransformUriTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.*",
                "--output_collections", "monster,seasmeStreet",
                "--output_uri_replace", ".*data/,/",
                "--output_uri_prefix", "/monster");
        thenDocumentsInMonsterCollection(8);
        Fragment frag = client.parseUri("/monster/bigbird.xml", "monster", "seasmeStreet");
        frag.assertElementValue("/monster/name", "BigBird");
    }

    @Test
    public void loadBinaryDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/binary/*.*",
                "--document_type", "binary",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadTextDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.txt",
                "--document_type", "text",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test(expected=AssertionError.class)
    public void inputFilePathExceptionTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--document_type", "text",
                "--output_collections", "monster,seasmeStreet");
    }

    public void thenDocumentsInMonsterCollection(int expectedCount) {
        client.assertCollectionSize("Expect 2 docs in monster collection", "monster", expectedCount);
        client.assertCollectionSize("Expect 2 docs in monster collection", "seasmeStreet", expectedCount);
    }

}
