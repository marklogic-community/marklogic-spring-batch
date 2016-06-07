package com.marklogic.spring.batch.job;

import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ContextConfiguration(classes = {
        com.marklogic.client.spring.BasicConfig.class,
        com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJob.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
}, loader = com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJobTest.CustomAnnotationConfigContextLoader.class
)
public class LoadDocumentsFromDirectoryJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private ApplicationContext context;

    @Test
    public void loadManyFilesTest() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        ClientTestHelper client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
        Fragment frag = client.parseUri("/Grover");
        frag.assertElementExists("/monster/name[text() = 'Grover']");
        frag = client.parseUri("/Elmo");
        frag.assertElementExists("/monster/name[text() = 'Elmo']");
        try {
            client.parseUri("/BigBird");
        } catch (ResourceNotFoundException ex) {
            assertNotNull(ex);
        }

    }

    public static class CustomAnnotationConfigContextLoader extends AnnotationConfigContextLoader {

        MockPropertySource source;

        @Override
        protected void customizeContext(GenericApplicationContext context) {
            source = new MockPropertySource();
            source.withProperty("input_file_path", "data/*.xml");
            source.withProperty("input_file_pattern", "(elmo|grover).xml");
            source.withProperty("uri_id", "/monster/name");
            source.withProperty("document_type", "xml");
            context.getEnvironment().getPropertySources().addFirst(source);
        }
    }

}
