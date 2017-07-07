package com.marklogic.spring.batch.samples;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.ImportDocumentsAndExtractTextJobConfig.class} )
public class ImportDocumentsAndExtractTextJobTest extends AbstractJobRunnerTest {

    @Test
    public void importOneDocxTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        jpb.addString("input_file_path", "src/test/resources/binary/*.docx");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());

        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("Hello.docx.xml", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        String value = frag.getElementValue("/html:html/html:body/html:p");
        frag.assertElementValue("/html:html/html:body/html:p", value);
    }
}
