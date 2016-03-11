package com.marklogic.spring.batch.core.explore;

import java.util.List;

import org.junit.Test;
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
public class GetJobExecutionsFromJobExplorerTest extends AbstractSpringBatchTest {

	
	@Autowired
	private JobExplorer jobExplorer;
	
	@Autowired
	private JobRepository jobRepository;
	
	private JobInstance jobInstance;
	private JobExecution jobExec;
	private List<JobExecution> jobExecutions;
	private Long jobExecId;
	
	@Test
	public void getJobExecutionByJobInstanceTest() throws Exception {
		givenAJobExecution();
		whenGetJobExecutionByJobInstanceFromJobExplorer();
		assertEquals(1, jobExecutions.size());
		thenVerifyJobExecution();
	}

	@Test
	public void getJobExecutionByIdTest() throws Exception {
		givenAJobExecution();
		whenGetJobExecutionFromJobExplorer();
		thenVerifyJobExecution();
	}

	private void thenVerifyJobExecution() {
		assertEquals("Joe Cool", jobExec.getJobParameters().getString("stringTest"));
		assertEquals("sampleJob", jobExec.getJobInstance().getJobName());	
	}
	
	private void whenGetJobExecutionByJobInstanceFromJobExplorer() {
		jobExecutions = jobExplorer.getJobExecutions(jobInstance);
		jobExec = jobExecutions.get(0);
	}

	private void whenGetJobExecutionFromJobExplorer() {
		jobExec = jobExplorer.getJobExecution(jobExecId);
	}

	private void givenAJobExecution() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {	
		jobInstance = jobRepository.createJobInstance("sampleJob", JobParametersTestUtils.getJobParameters());
		JobExecution tempJob = jobRepository.createJobExecution(jobInstance, JobParametersTestUtils.getJobParameters(), "Abc");
		jobInstance = tempJob.getJobInstance();
		jobExecId = tempJob.getId();
	}

		
}
