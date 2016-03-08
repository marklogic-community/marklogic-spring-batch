package com.marklogic.spring.batch.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.test.JobRepositoryTestUtils;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class IsJobInstanceExistTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;
	
	private final String jobName = "job";
	private JobParameters params;
	
	@Before 
	public void JobExecution() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobExecution jobExec = jobRepositoryTestUtils.createJobExecutions(1).get(0);
		long jobParameterValue = jobExec.getJobParameters().getLong("count");
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addLong("count", jobParameterValue);
		params = builder.toJobParameters();
	}
	
	@Test
	public void verifyJobInstanceExistsTest()  {	
		assertTrue(jobRepository.isJobInstanceExists(jobName, params));
	}
	
	@Test
	public void verifyJobInstanceDoesNotExistWithJobNameTest() {
		assertFalse(jobRepository.isJobInstanceExists(jobName + "-test", params));
	}
	
	@Test 
	public void verifyJobInstanceDoesNotExistWithJobParametersTest() {
		JobParametersBuilder builder = new JobParametersBuilder(params);
		builder.addLong("second", 100L, true);
		assertFalse(jobRepository.isJobInstanceExists(jobName, builder.toJobParameters()));
	}

}
