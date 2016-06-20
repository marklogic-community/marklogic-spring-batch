package com.marklogic.spring.batch.job;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.config.AbstractJobsJobTest;
import com.marklogic.spring.batch.config.LoadDocumentsFromDirectoryConfig;
import com.marklogic.spring.batch.config.LoadImagesFromDirectoryConfig;
import org.junit.Test;


public class LoadBinariesFromDirectoryTest extends AbstractJobsJobTest {

    @Test
    public void importOneJpegTest() {
        runJob(LoadImagesFromDirectoryConfig.class,
                "--input_file_path", "binary/*.jpg");
        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("test.xml", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        frag.assertElementExists("/html:html/html:head/html:meta[1][@content = 'Corbis']");
        frag.assertElementExists("/html:html/html:head/html:meta[1][@name = 'Artist']");
    }

    @Test
    public void importOneDocxTest() {
        runJob(LoadDocumentsFromDirectoryConfig.class,
                "--input_file_path", "binary/*.docx");
        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("test.xml", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        String value = frag.getElementValue("/html:html/html:body/html:p");
        frag.assertElementValue("/html:html/html:body/html:p", value);
    }
}
