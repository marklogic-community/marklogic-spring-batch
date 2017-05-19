package com.marklogic.spring.batch.config;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import org.junit.Test;

public class LoadBinariesFromDirectoryTest extends AbstractFileImportTest {

    @Test
    public void importOneJpegTest() {
        runJob(LoadImagesFromDirectoryConfig.class,
                "--input_file_path", "src/test/resources/binary/*.jpg");
        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("Penguins.jpg", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        frag.assertElementExists("/html:html/html:head/html:meta[1][@content = 'Corbis']");
        frag.assertElementExists("/html:html/html:head/html:meta[1][@name = 'Artist']");
    }

    @Test
    public void importOneDocxTest() {
        runJob(ImportDocumentsAndExtractTextConfig.class,
                "--input_file_path", "src/test/resources/binary/*.docx");
        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("Hello.docx", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        String value = frag.getElementValue("/html:html/html:body/html:p");
        frag.assertElementValue("/html:html/html:body/html:p", value);
    }
}
