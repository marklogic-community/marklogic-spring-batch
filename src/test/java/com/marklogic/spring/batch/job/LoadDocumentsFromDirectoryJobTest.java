package com.marklogic.spring.batch.job;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = {
		com.marklogic.junit.spring.BasicTestConfig.class,
		com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJob.class,
		com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class }, loader = com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJobTest.CustomAnnotationConfigContextLoader.class)
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

	}

	public static class CustomAnnotationConfigContextLoader extends
			AnnotationConfigContextLoader {

		MockPropertySource source;

		@Override
		protected void customizeContext(GenericApplicationContext context) {
			source = new MockPropertySource();
			source.withProperty("input_file_path", "data/*.json");
			source.withProperty("input_file_pattern", "(elmo|grover).json");
			context.getEnvironment().getPropertySources().addFirst(source);
		}
	}

}
