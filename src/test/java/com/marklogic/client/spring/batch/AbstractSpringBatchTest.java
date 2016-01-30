package com.marklogic.client.spring.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
	
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	protected StepBuilderFactory stepBuilderFactory;
	
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
	
	@Override
	protected NamespaceProvider getNamespaceProvider() {
		return nsProvider;
	}
	 
	/**
	     * Convenience method for testing a single step; the subclass doesn't have to bother with creating a job.
	     * 
	     * @param step
	     * @return
	 */
	 protected void launchJobWithStep(Step step) {
		 Job job = jobBuilderFactory.get("testJob").start(step).build();
		 jobLauncherTestUtils.setJob(job);

	     JobExecution jobExecution = null;
	     try {
	    	 jobExecution = jobLauncherTestUtils.launchJob();
	     } catch (Exception e) {
	    	 throw new RuntimeException(e);
	     }
	     assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	     logger.info("Job execution time: " + (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()));
	 }

}
