package com.marklogic.spring.batch;

import org.junit.Test;

import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.item.CollectionUrisReader;
import com.marklogic.spring.batch.item.DeleteUriWriter;

public class DeleteUrisTest extends AbstractSpringBatchTest {

    private final static int NUMBER_OF_DOCUMENTS_TO_CREATE = 2500;
    private final static int NUMBER_OF_DOCUMENTS_TO_DELETE_AT_ONE_TIME = 100;

    @Test
    public void test() {
        givenSomeDocumentsInTheTestCollection();
        whenAJobIsRunToDeleteAllTheUrisInTheTestCollection();
        thenTheTestCollectionIsNowEmpty();
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
     * Launch a job with a step that uses CollectionUrisReader to read all the URIs from the "test" collection, and then
     * uses DeleteUriWriter to delete every URI that is read. The URIs are chunked together to avoid making a delete
     * call per URI.
     */
    private void whenAJobIsRunToDeleteAllTheUrisInTheTestCollection() {
        //launchJobWithStep(
          //      stepBuilderFactory.get("testStep").<String, String> chunk(NUMBER_OF_DOCUMENTS_TO_DELETE_AT_ONE_TIME)
            //            .reader(new CollectionUrisReader(getClient(), "test")).writer(new DeleteUriWriter(getClient()))
              //          .build());

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
}
