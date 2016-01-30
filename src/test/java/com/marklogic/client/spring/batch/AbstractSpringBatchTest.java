package com.marklogic.client.spring.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobRepositoryTestUtils;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.SpringBatchConfig.class, com.marklogic.client.spring.batch.DatabaseConfig.class })
@TestPropertySource("classpath:config/application-test.properties")
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	@Autowired
	protected JobLauncher jobLauncher;
	
	@Autowired
	protected JobRepository jobRepository;
	
	@Autowired
	protected JobExplorer jobExplorer;
	
	@Autowired
	protected DatabaseClientProvider databaseClientProvider;
	
	@Autowired
	protected DatabaseClientConfig databaseClientConfig;
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	protected JobLauncherTestUtils jobLauncherTestUtils;

	protected JobRepositoryTestUtils jobRepositoryTestUtils;
	
	protected Log log = LogFactory.getLog(AbstractSpringBatchTest.class);
	
	public AbstractSpringBatchTest() {
		super();
		nsProvider = new SpringBatchNamespaceProvider();
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobRepositoryTestUtils = new JobRepositoryTestUtils();
	}
	
	@Before
	public void beforeTest() {
		log.info(databaseClientConfig.toString());
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
	}
	
	public void test() {
		System.out.println(databaseClientConfig.getPort());
	}
	
	 @Override
	 protected NamespaceProvider getNamespaceProvider() {
		 return nsProvider;
	 }

}
