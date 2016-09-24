package com.marklogic.spring.batch.config;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import org.junit.Test;

public class DeleteDocumentsTest extends AbstractFileImportTest {

    private final static int NUMBER_OF_DOCUMENTS_TO_CREATE = 200;

    /**
     * Note that since this uses a Config class that only has one Job defined in it, there's no need
     * to specify the name of a job.
     * <p>
     * Now enabling the ML JobRepo as well. We expect the document created by it to still exist since our
     * "Delete" job deletes documents in a different collection.
     */
    @Test
    public void testJob() {
        givenSomeDocumentsInTheTestCollection();
        runJobWithMarkLogicJobRepository(DeleteDocumentsConfig.class, "--collections", "test");
        thenTheTestCollectionIsNowEmpty();
        andTheJobInstanceDocumentExists();
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

    /**
     * Use the Client API to verify that the test collection is now empty.
     */
    private void thenTheTestCollectionIsNowEmpty() {
        String result = getClient().newServerEval().xquery("collection('test')").evalAs(String.class);
        assertNull(
                "The test collection should be empty because the job deleted all the documents we inserted into the collection",
                result);
    }

    private void andTheJobInstanceDocumentExists() {
        String result = getClient().newServerEval().xquery("collection('http://marklogic.com/spring-batch/job-instance')").evalAs(String.class);
        Fragment f = parse(result);
        f.assertElementValue("/msb:mlJobInstance/msb:jobInstance/msb:jobName", "deleteDocumentsJob");
    }
}
