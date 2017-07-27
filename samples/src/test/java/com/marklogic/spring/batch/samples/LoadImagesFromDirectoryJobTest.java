package com.marklogic.spring.batch.samples;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {LoadImagesFromDirectoryJobConfig.class} )
public class LoadImagesFromDirectoryJobTest extends AbstractJobRunnerTest {

    @Test
    public void importOneJpegTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        jpb.addString("input_file_path", "src/test/resources/binary/*.jpg");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());

        XMLDocumentManager docMgr = getClient().newXMLDocumentManager();
        StringHandle handle = new StringHandle();
        docMgr.read("Penguins.jpg.xml", handle);
        Fragment frag = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
        frag.assertElementExists("/html:html/html:head/html:meta[1][@content = 'Corbis']");
        frag.assertElementExists("/html:html/html:head/html:meta[1][@name = 'Artist']");
        getClientTestHelper().assertCollectionSize("Expecting one jobRepo docs", "http://marklogic.com/spring-batch/job-instance", 1);
    }
}
