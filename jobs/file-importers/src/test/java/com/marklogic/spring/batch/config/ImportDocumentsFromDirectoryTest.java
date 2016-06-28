package com.marklogic.spring.batch.config;

import com.marklogic.junit.ClientTestHelper;
import org.junit.Test;

public class ImportDocumentsFromDirectoryTest extends AbstractFileImportTest {

    @Test
    public void loadXmlDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.xml",
                "--input_file_pattern", "(elmo|grover).xml",
                "--document_type", "xml",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadJsonDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.json",
                "--input_file_pattern", "(elmo|grover).json",
                "--document_type", "json",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadXmlJsonAndTextDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.*",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(8);
    }

    @Test
    public void loadBinaryDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "binary/*.*",
                "--document_type", "binary",
                "--output_collections", "monster,seasmeStreet");
        thenDocumentsInMonsterCollection(2);
    }

    @Test
    public void loadTextDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.txt",
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
        ClientTestHelper client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
        client.assertCollectionSize("Expect 2 docs in monster collection", "monster", expectedCount);
        client.assertCollectionSize("Expect 2 docs in monster collection", "seasmeStreet", expectedCount);
    }

}
