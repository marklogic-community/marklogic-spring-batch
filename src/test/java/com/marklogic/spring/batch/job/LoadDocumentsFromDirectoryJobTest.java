package com.marklogic.spring.batch.job;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ContextConfiguration(classes = {
        com.marklogic.junit.spring.BasicTestConfig.class,
        com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJob.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
    } , loader = com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJobTest.CustomAnnotationConfigContextLoader.class
)
public class LoadDocumentsFromDirectoryJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private ApplicationContext context;

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        ClientTestHelper client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
        Fragment frag = client.parseUri("/Grover");
        frag.assertElementExists("/monster/name[text() = 'Grover']");
    }

    public static class CustomAnnotationConfigContextLoader extends AnnotationConfigContextLoader {
        @Override
        protected void customizeContext(GenericApplicationContext context) {
            MockPropertySource source = new MockPropertySource()
                    .withProperty("input_file_path", "data/grover.xml");
            source.withProperty("uri_id", "/monster/name" );
            context.getEnvironment().getPropertySources().addFirst(source);
        }
    }

}
