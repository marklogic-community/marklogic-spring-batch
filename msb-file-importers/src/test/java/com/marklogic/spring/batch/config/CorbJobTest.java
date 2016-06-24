package com.marklogic.spring.batch.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import org.junit.Test;

public class CorbJobTest extends AbstractFileImportTest {

    @Test
    public void runCorbJobTest() {
        givenATestDocument();
        runJobWithMarkLogicJobRepository(CorbConfig.class,
                "--uris_module", "/ext/corb/uris.xqy",
                "--transform_module", "/ext/corb/process.xqy");
        thenVerifyCorbWorked();
    }

    private void givenATestDocument() {
        XMLDocumentManager xmlMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle("<hello />");
        DocumentMetadataHandle h = new DocumentMetadataHandle().withCollections("test");
        xmlMgr.write("/doc.xml", h, handle);
    }

    private void thenVerifyCorbWorked() {
        DatabaseClient client = getClient();
        XMLDocumentManager xmlMgr = client.newXMLDocumentManager();
        StringHandle handle = xmlMgr.read("/doc.xml", new StringHandle());
        Fragment fragment = new Fragment(handle.get());
        fragment.assertElementExists("/goodbye");
    }
}
