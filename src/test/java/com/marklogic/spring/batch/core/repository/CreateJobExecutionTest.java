package com.marklogic.spring.batch.core.repository;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobParametersTestUtils;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class CreateJobExecutionTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	private long jobExecId;

	
	@Test
	public void createJobExecutionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		givenAJob();
		whenAJobExecutionIsCreated();
		thenVerifyJobExecutionExists();
	}


	private void thenVerifyJobExecutionExists() {
		JobExecution jobExec = jobExplorer.getJobExecution(jobExecId);
		assertEquals(jobExecId, jobExec.getId().longValue());
		
	}


	private void whenAJobExecutionIsCreated() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobExecution jobExecution = jobRepository.createJobExecution("testJob", JobParametersTestUtils.getJobParameters());	
		jobExecId = jobExecution.getId();
	}


	private void givenAJob() {
			
	}
}
