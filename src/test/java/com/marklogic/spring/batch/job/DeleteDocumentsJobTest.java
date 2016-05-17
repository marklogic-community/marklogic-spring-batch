package com.marklogic.spring.batch.job;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.junit.spring.BasicTestConfig.class,
        com.marklogic.spring.batch.job.DeleteDocumentsJob.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
})
public class DeleteDocumentsJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private final static int NUMBER_OF_DOCUMENTS_TO_CREATE = 2500;

    @Test
    public void testJob() throws Exception {
        givenSomeDocumentsInTheTestCollection();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    }

    /**
     * Use the Client API to load a bunch of documents. This has no dependency on Spring Batch, we're just loading some
     * documents that we'll then delete using Spring Batch.
     */
    private void givenSomeDocumentsInTheTestCollection() {
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        DocumentWriteSet set = mgr.newWriteSet();
        int count = 0;
        // Write the documents in batches of 100
        logger.info("Loading " + NUMBER_OF_DOCUMENTS_TO_CREATE + " documents");
        long start = System.currentTimeMillis();
        for (int i = 1; i <= NUMBER_OF_DOCUMENTS_TO_CREATE; i++) {
            set.add("/doc/" + i + ".xml", new DocumentMetadataHandle().withCollections("test"),
                    new StringHandle("<test>" + i + "</test>"));
            count++;
            if (count == 100) {
                mgr.write(set);
                set = mgr.newWriteSet();
                count = 0;
            }
        }
        logger.info("Loaded documents in " + (System.currentTimeMillis() - start) + "ms");
    }

}
