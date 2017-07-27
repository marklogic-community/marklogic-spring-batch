package com.marklogic.spring.batch.samples;

import com.marklogic.spring.batch.test.AbstractJobRunnerTest;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.ExportContentFromMarkLogicJobConfig.class})
public class ExportContentFromMarkLogicJobTest extends AbstractJobRunnerTest {

    public void insertDocument(String uri, String collections, String xml) {
        DatabaseClient client = getClient();
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        String[] collectionArray = collections.split(",");
        metadata.withCollections(collectionArray);
        docMgr.write(uri, metadata, new StringHandle(xml));
    }

    @Before
    public void setup() throws IOException {
        for (int i = 0; i < 102; i++) {
            insertDocument("/sample/doc" + i, "test", "<hello>sample-" + i + "</hello>");
        }
        Resource r = new FileSystemResource("./output-1.xml");
        if (r.exists()) {
            r.getFile().delete();
        }

        r = new FileSystemResource("./output-2.xml");
        if (r.exists()) {
            r.getFile().delete();
        }

    }
    
    @Test
    public void writeDocumentsToFileSystemTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_file_path", "./");
        jpb.addString("collection", "test");

        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting 102 uris in test collection", "test", 102);
        Resource r = new FileSystemResource("./output-1.xml");
        assertTrue(r.exists());
        r = new FileSystemResource("./output-2.xml");
        assertTrue(r.exists());
    }

}
