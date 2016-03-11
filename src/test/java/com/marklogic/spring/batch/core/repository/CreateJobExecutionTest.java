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
	
	private JobExecution jobExecution;
	
	@Test
	public void createJobExecutionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		givenAJobInstance();
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
		thenVerifyAJobExecutionIsPersisted();
	}
	/*
	@Test(expected=JobExecutionAlreadyRunningException.class)
	public void throwJobExecutionAlreadyRunningExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		whenAJobExecutionIsCreated();
		whenAJobExecutionIsCreated();
		thenVerifyJobExecutionExists();
	}

	@Ignore
	@Test(expected=JobInstanceAlreadyCompleteException.class)
	public void throwJobInstanceAlreadyCompleteException() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		givenACompletedJobExecution();
		whenAJobExecutionIsCreated();
	}
*/
	private void givenAJobInstance() {
		jobRepository.createJobInstance("testJob", JobParametersTestUtils.getJobParameters());
	}
	
	private void whenAJobExecutionIsCreatedFromJobNameAndParameters() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobExecution = jobRepository.createJobExecution("testJob", JobParametersTestUtils.getJobParameters());
	}
	
	private void thenVerifyAJobExecutionIsPersisted() {
		JobExecution jobExec = jobExplorer.getJobExecution(jobExecution.getId());
		assertNotNull(jobExec);
	}

}
