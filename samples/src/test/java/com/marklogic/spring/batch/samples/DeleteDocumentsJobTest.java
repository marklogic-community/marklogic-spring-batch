package com.marklogic.spring.batch.samples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.DeleteDocumentsJobConfig.class} )
public class DeleteDocumentsJobTest extends AbstractJobRunnerTest {

    public void insertDocument(String uri, String collections, String xml) {
        DatabaseClient client = getClient();
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        String[] collectionArray = collections.split(",");
        metadata.withCollections(collectionArray);
        docMgr.write(uri, metadata, new StringHandle(xml));
    }

    @Before
    public void setup() {
        for (int i = 0; i < 10; i++) {
            insertDocument("doc" + i, "monster", "<hello />");
        }
    }

    @Test
    @Ignore
    public void deleteMonsterCollectionWithDmsdkTest() throws Exception {
        Assume.assumeTrue(isMarkLogic9());
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        jpb.addString("marklogic_version", "9");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting zero documents in monster collection", "monster", 0);
    }


    @Test
    public void deleteMonsterCollectionTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting zero documents in monster collection", "monster", 0);
    }

}
