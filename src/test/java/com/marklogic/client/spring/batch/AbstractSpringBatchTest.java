package com.marklogic.client.spring.batch;

import org.junit.Before;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobRepositoryTestUtils;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.SpringBatchConfig.class, com.marklogic.client.spring.DatabaseConfig.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	@Autowired
	protected JobLauncher jobLauncher;
	
	@Autowired
	protected JobRepository jobRepository;
	
	@Autowired
	protected JobExplorer jobExplorer;
	
	@Autowired
	protected DatabaseClientProvider databaseClientProvider;
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	protected JobLauncherTestUtils jobLauncherTestUtils;

	protected JobRepositoryTestUtils jobRepositoryTestUtils;
	
	public AbstractSpringBatchTest() {
		super();
		nsProvider = new SpringBatchNamespaceProvider();
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobRepositoryTestUtils = new JobRepositoryTestUtils();
	}
	
	@Before
	public void beforeTest() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
		jobLauncherTestUtils.setJobRepository(jobRepository);
	}
	
	 @Override
	 protected NamespaceProvider getNamespaceProvider() {
		 return nsProvider;
	 }

}
