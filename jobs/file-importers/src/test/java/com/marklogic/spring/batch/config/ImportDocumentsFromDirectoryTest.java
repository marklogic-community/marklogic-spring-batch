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
        thenTwoDocumentsInMonsterCollection();
    }

    @Test
    public void loadJsonDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.json",
                "--input_file_pattern", "(elmo|grover).json",
                "--document_type", "json",
                "--output_collections", "monster,seasmeStreet");
        thenTwoDocumentsInMonsterCollection();
    }

    @Test
    public void loadBinaryDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "binary/*.*",
                "--document_type", "binary",
                "--output_collections", "monster,seasmeStreet");
        thenTwoDocumentsInMonsterCollection();
    }

    @Test
    public void loadTextDocumentsTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--input_file_path", "data/*.txt",
                "--document_type", "text",
                "--output_collections", "monster,seasmeStreet");
        thenTwoDocumentsInMonsterCollection();
    }

    @Test(expected=AssertionError.class)
    public void inputFilePathExceptionTest() {
        runJobWithMarkLogicJobRepository(
                ImportDocumentsFromDirectoryConfig.class,
                "--document_type", "text",
                "--output_collections", "monster,seasmeStreet");
    }

    public void thenTwoDocumentsInMonsterCollection() {
        ClientTestHelper client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
        client.assertCollectionSize("Expect 2 docs in monster collection", "monster", 2);
        client.assertCollectionSize("Expect 2 docs in monster collection", "seasmeStreet", 2);
    }

}
