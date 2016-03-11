package com.marklogic.spring.batch.core.repository;

import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
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
	public void createJobExecutionFromJobNameAndParametersTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
		thenVerifyAJobExecutionIsPersisted();
	}
	
	@Test
	public void createJobExecutionFromJobInstanceAndParametersTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		whenAJobExecutionIsCreatedFromJobInstance();
		thenVerifyAJobExecutionIsPersisted();
	}

	@Test(expected=JobExecutionAlreadyRunningException.class)
	public void throwJobExecutionAlreadyRunningExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
	}

	@Test(expected=JobInstanceAlreadyCompleteException.class)
	public void throwJobInstanceAlreadyCompleteException() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
		jobExecution.setStatus(BatchStatus.COMPLETED);
		jobRepository.update(jobExecution);
		whenAJobExecutionIsCreatedFromJobNameAndParameters();
	}

	
	private void whenAJobExecutionIsCreatedFromJobNameAndParameters() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		jobExecution = jobRepository.createJobExecution("testJob", JobParametersTestUtils.getJobParameters());
		
	}
	
	private void whenAJobExecutionIsCreatedFromJobInstance() {
		JobInstance jobInstance = new JobInstance(123L, "testJob");
		jobExecution = jobRepository.createJobExecution(jobInstance, JobParametersTestUtils.getJobParameters(), "placeholder");
	}
	
	private void thenVerifyAJobExecutionIsPersisted() {
		JobExecution jobExec = jobExplorer.getJobExecution(jobExecution.getId());
		assertNotNull(jobExec);
		assertEquals(jobExec.getJobInstance().getJobName(), "testJob");
	}

}
