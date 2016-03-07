package com.marklogic.spring.batch.core.repository;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class IsJobInstanceExistTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobRepository jobRepository;
	
	private JobExecution jobExec;
	
	@Before 
	public void createJobExecution() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobExec = newJobRepositoryTestUtils().createJobExecutions(1).get(0);	
	}
	
	@Test
	public void verifyJobInstanceExistsTest()  {		
		String jobName = jobExec.getJobInstance().getJobName();
		JobParameters params = jobExec.getJobParameters();
		assertTrue(jobRepository.isJobInstanceExists(jobName, params));
	}
	
	@Test
	public void verifyJobInstanceDoesNotExist() {
		String jobName = jobExec.getJobInstance().getJobName();
		JobParameters params = jobExec.getJobParameters();
		assertFalse(jobRepository.isJobInstanceExists(jobName + "-test", params));
	}

}
