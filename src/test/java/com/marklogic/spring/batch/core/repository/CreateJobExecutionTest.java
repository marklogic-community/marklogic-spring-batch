package com.marklogic.spring.batch.core.repository;

import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
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
	
	private final String JOB_NAME = "testJob";
	
	@Test
	public void createJobExecutionFromJobNameAndParametersTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobExecution jobExecution = jobRepository.createJobExecution(JOB_NAME, JobParametersTestUtils.getJobParameters());
		thenVerifyAJobExecutionIsPersisted(jobExecution);
	}
	
	@Test
	public void createJobExecutionFromJobInstanceAndParametersTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobInstance jobInstance = givenAJobInstance();
		JobExecution jobExecution = whenAJobExecutionIsCreated(jobInstance, JobParametersTestUtils.getJobParameters());
		thenVerifyAJobExecutionIsPersisted(jobExecution);
	}




	
	@Test
	public void createJobExecutionWhenJobHasFailedTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobInstance jobInstance = givenAJobInstance();
		JobParameters jobParameters = JobParametersTestUtils.getJobParameters();
		jobExecution = jobRepository.createJobExecution(jobInstance, jobParameters, null);
		jobExecution.setStatus(BatchStatus.FAILED);
		jobRepository.update(jobExecution);
		jobRepository.createJobExecution(jobInstance.getJobName(), jobParameters);
		assertEquals(2, jobExplorer.getJobExecutions(jobInstance).size());
	}
	
	private JobInstance givenAJobInstance() {
		return new JobInstance(123L, JOB_NAME);
	}
	
	private JobExecution whenAJobExecutionIsCreated(JobInstance jobInstance, JobParameters jobParameters) {
		return jobRepository.createJobExecution(jobInstance, jobParameters, null);
	}
	
	private void thenVerifyAJobExecutionIsPersisted(JobExecution expectedJobExecution) {
		JobExecution jobExec = jobExplorer.getJobExecution(expectedJobExecution.getId());
		assertNotNull(jobExec);
		assertEquals(jobExec.getJobInstance().getJobName(), JOB_NAME);
	}

}
