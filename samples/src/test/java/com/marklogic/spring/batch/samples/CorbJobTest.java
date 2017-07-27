package com.marklogic.spring.batch.samples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.CorbJobConfig.class})
public class CorbJobTest extends AbstractJobRunnerTest {

    DatabaseClient client;
    final String URIS_MODULE = "/ext/uris.xqy";
    final String PROCESS_MODULE = "/ext/process.xqy";

    public void insertDocument(String uri, String collections, String xml) {
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("test");
        docMgr.write(uri, metadata, new StringHandle(xml));
    }

    @Before
    public void setup() throws IOException {
        client = getClient();
        for (int i = 0; i < 150; i++) {
            insertDocument("/sample/doc" + i, "test", "<hello>sample-" + i + "</hello>");
        }

        ExtensionLibrariesManager libMgr = client.newServerConfigManager().newExtensionLibrariesManager();
        StringHandle handle = new StringHandle("cts:uris('', (), cts:collection-query('test'))").withFormat(Format.TEXT);
        libMgr.writeAs(URIS_MODULE, handle);

        StringBuilder process = new StringBuilder("xquery version '1.0-ml';\n");
        process.append("declare variable $URI external;\n");
        process.append("let $doc := <corb><uri>{ $URI }</uri></corb>\n");
        process.append("return xdmp:document-insert($URI, $doc, (), ('test', 'corb'))");
        handle = new StringHandle(process.toString()).withFormat(Format.TEXT);
        libMgr.writeAs(PROCESS_MODULE, handle);
    }

    @Test
    public void runSimpleCorbJob() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("URIS-MODULE", URIS_MODULE);
        jpb.addString("PROCESS-MODULE", PROCESS_MODULE);
        jpb.addLong("BATCH-SIZE", 50L);

        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

}
