package com.marklogic.spring.batch.samples;

import com.marklogic.spring.batch.test.AbstractJobTest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.ExportContentFromMarkLogicJob.class})
public class ExportContentFromMarkLogicJobTest extends AbstractJobTest {

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
            insertDocument("/sample/doc" + i, "test", "<hello>sample-" + i + "</hello>");
        }
    }
    
    @Test
    public void findURIsInDatabaseTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_file_path", "/temp");
        jpb.addString("collection", "test");

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting 10 uris in test collection", "test", 10);
    }

}
