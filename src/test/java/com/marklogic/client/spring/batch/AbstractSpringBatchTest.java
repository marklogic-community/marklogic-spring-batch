package com.marklogic.client.spring.batch;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@SpringApplicationConfiguration(classes = { com.marklogic.client.spring.DatabaseConfig.class, com.marklogic.client.spring.batch.SpringBatchConfig.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	protected JobLauncherTestUtils jobLauncherTestUtils;
	protected JobRepositoryTestUtils jobRepositoryTestUtils;
	
	@Autowired
	protected JobRepository jobRepository;
	
	@Autowired
	protected JobExplorer jobExplorer;
	
	@Autowired
	protected DatabaseClientProvider databaseClientProvider;
	
	public AbstractSpringBatchTest() {
		super();
		nsProvider = new SpringBatchNamespaceProvider();
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobRepositoryTestUtils = new JobRepositoryTestUtils();
	}
	
	 @Override
	 protected NamespaceProvider getNamespaceProvider() {
		 return nsProvider;
	 }

}
