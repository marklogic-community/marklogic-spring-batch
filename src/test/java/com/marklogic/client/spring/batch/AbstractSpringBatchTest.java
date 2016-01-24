package com.marklogic.client.spring.batch;

import org.junit.Before;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.SpringBatchConfig.class, com.marklogic.client.spring.DatabaseConfig.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	@Autowired
	protected JobLauncher jobLauncher;
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	protected JobLauncherTestUtils jobLauncherTestUtils;
	
	public AbstractSpringBatchTest() {
		super();
		nsProvider = new SpringBatchNamespaceProvider();
	}
	
	@Before
	public void beforeTest() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobLauncher(jobLauncher);
	}
	
	 @Override
	 protected NamespaceProvider getNamespaceProvider() {
		 return nsProvider;
	 }

}
