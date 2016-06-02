package com.marklogic.spring.batch.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
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
        com.marklogic.junit.spring.BasicTestConfig.class,
        LoadDocumentsFromDirectoryJob.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
}, loader = LoadJsonDocumentsFromDirectoryJobTest.CustomAnnotationConfigContextLoader.class
)
public class LoadJsonDocumentsFromDirectoryJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Autowired
    private ApplicationContext context;

    @Test
    public void loadManyFilesTest() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals("COMPLETED", jobExecution.getStatus().name());

        DatabaseClient client = getClientProvider().getDatabaseClient();
        JSONDocumentManager jsonMgr = client.newJSONDocumentManager();
        QueryManager queryMgr = client.newQueryManager();
        StringQueryDefinition qd = queryMgr.newStringDefinition();
        qd.setCriteria("Elmo OR Grover");
        assertEquals(2, jsonMgr.search(qd, 0).getTotalSize());
    }

    public static class CustomAnnotationConfigContextLoader extends AnnotationConfigContextLoader {

        MockPropertySource source;

        @Override
        protected void customizeContext(GenericApplicationContext context) {
            source = new MockPropertySource();
            source.withProperty("input_file_path", "data/*.json");
            source.withProperty("input_file_pattern", "(elmo|grover).json");
            source.withProperty("document_type", "json");

            context.getEnvironment().getPropertySources().addFirst(source);
        }
    }

}
