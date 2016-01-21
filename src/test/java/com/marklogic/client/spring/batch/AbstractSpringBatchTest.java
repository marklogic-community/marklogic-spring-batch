package com.marklogic.client.spring.batch;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.DatabaseConfig.class, com.marklogic.client.spring.batch.SpringBatchConfig.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	protected JobLauncherTestUtils jobLauncherTestUtils;
	protected JobRepositoryTestUtils jobRepositoryTestUtils;
	
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
