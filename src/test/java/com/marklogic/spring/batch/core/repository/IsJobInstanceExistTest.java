package com.marklogic.spring.batch.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.test.JobRepositoryTestUtils;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class IsJobInstanceExistTest extends AbstractSpringBatchTest {
	
	@Autowired 
	private JobRepositoryTestUtils jobRepositoryTestUtils;
	
	private JobExecution jobExec;
	
	@Before 
	public void createJobExecution() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobExec = jobRepositoryTestUtils.createJobExecutions(1).get(0);
	}
	
	@Test
	public void verifyJobInstanceExistsTest()  {	
		String jobName = "job";
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addLong("count", 0L);
		JobParameters params = builder.toJobParameters();
		assertTrue(jobRepository.isJobInstanceExists(jobName, params));
	}
	
	@Test
	public void verifyJobInstanceDoesNotExistTest() {
		String jobName = jobExec.getJobInstance().getJobName();
		JobParameters params = jobExec.getJobParameters();
		assertFalse(jobRepository.isJobInstanceExists(jobName + "-test", params));
	}
	
	@Test 
	public void verifyJobInstanceWithJobParametersTest() {
		
		
	}

}
