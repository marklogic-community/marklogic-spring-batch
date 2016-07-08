package com.marklogic.spring.batch.config;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import org.junit.Before;
import org.junit.Test;

public class ImportDocumentsFromDirectoryTest extends AbstractFileImportTest {

    private ClientTestHelper client;

    @Before
    public void setup() {
        client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
    }

    @Test
    public void loadXmlDocumentsTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.xml",
                "--input_file_pattern", "(elmo|grover).xml",
                "--document_type", "xml",
                "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadJsonDocumentsTest() {
        runJob(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/data/*.json",
                "--input_file_pattern", "(elmo|grover).json",
                "--document_type", "json",
                "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(2);
    }

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

    public void thenDocumentsInMonsterCollection(int expectedCount) {
        client.assertCollectionSize("Expect 2 docs in monster collection", "monster", expectedCount);
        client.assertCollectionSize("Expect 2 docs in monster collection", "sesameStreet", expectedCount);
    }

}
