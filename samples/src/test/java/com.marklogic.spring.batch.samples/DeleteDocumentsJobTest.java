package com.marklogic.spring.batch.samples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.DeleteDocumentsJob.class} )
public class DeleteDocumentsJobTest extends AbstractJobTest {

    public void insertDocument(String uri, String collections, String xml) {
        DatabaseClient client = databaseClientProvider.getDatabaseClient();
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        String[] collectionArray = collections.split(",");
        metadata.withCollections(collectionArray);
        docMgr.write(uri, metadata, new StringHandle(xml));
    }

    @Before
    public void setup() {
        Assume.assumeTrue(isMarkLogic9());
        for (int i = 0; i < 10; i++) {
            insertDocument("doc" + i, "monster", "<hello />");
        }
    }

    @Test
    public void deleteMonsterCollectionTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting zero documents in monster collection", "monster", 0);
    }

}
