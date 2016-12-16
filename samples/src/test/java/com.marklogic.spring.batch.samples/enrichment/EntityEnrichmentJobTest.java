package com.marklogic.spring.batch.samples.enrichment;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.enrichment.EntityEnrichmentJob.class} )
public class EntityEnrichmentJobTest extends AbstractJobTest {
    
    XMLDocumentManager docMgr;
    
    @Before
    public void setup() {
        DatabaseClient client = getClient();
        docMgr = client.newXMLDocumentManager();
        StringHandle text1 = new StringHandle("<doc><text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final.  Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday</text></doc>");
        DocumentMetadataHandle handle = new DocumentMetadataHandle();
        handle.withCollections("sourceXML");
        docMgr.write("hello.xml", handle, text1);
    }


    @Test
    public void enrichTextSnippetTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("tokenizer_model", "src/main/resources/nlp/tokenizer/en-token.bin");
        jpb.addString("named_entity_model", "src/main/resources/nlp/en/en-ner-person.bin");
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        //getClientTestHelper().assertCollectionSize("Expecting zero documents in monster collection", "monster", 0);
    }
}
