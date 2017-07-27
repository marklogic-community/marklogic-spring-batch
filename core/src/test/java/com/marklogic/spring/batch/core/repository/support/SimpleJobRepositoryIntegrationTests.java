package com.marklogic.spring.batch.core.repository.support;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import com.marklogic.spring.batch.core.job.JobSupport;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import com.marklogic.spring.batch.core.step.StepSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

public class SimpleJobRepositoryIntegrationTests extends AbstractJobRepositoryTest {

	private JobSupport job = new JobSupport("SimpleJobRepositoryIntegrationTestsJob");

	private JobParameters jobParameters = new JobParameters();
	
	@Before
	public void initialize() {
		initializeJobRepository();
	}

	/*
	 * Create two job executions for same job+parameters tuple. Check both
	 * executions belong to the same job instance and job.
	 */
	@Transactional
	@Test
	public void testCreateAndFind() throws Exception {

		job.setRestartable(true);

		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addString("stringKey", "stringValue").addLong("longKey", 1L).addDouble("doubleKey", 1.1).addDate(
				"dateKey", new Date(1L));
		JobParameters jobParams = builder.toJobParameters();

		JobExecution firstExecution = getJobRepository().createJobExecution(job.getName(), jobParams);
		firstExecution.setStartTime(new Date());
		assertNotNull(firstExecution.getLastUpdated());

		assertEquals(job.getName(), firstExecution.getJobInstance().getJobName());
		
		getJobRepository().update(firstExecution);
		firstExecution.setEndTime(new Date());
		getJobRepository().update(firstExecution);
		JobExecution secondExecution = getJobRepository().createJobExecution(job.getName(), jobParams);

		assertEquals(firstExecution.getJobInstance(), secondExecution.getJobInstance());
		assertEquals(job.getName(), secondExecution.getJobInstance().getJobName());
	}

	/*
	 * Create two job executions for same job+parameters tuple. Check both
	 * executions belong to the same job instance and job.
	 */
	@Transactional
	@Test
	public void testCreateAndFindWithNoStartDate() throws Exception {
		job.setRestartable(true);

		JobExecution firstExecution = getJobRepository().createJobExecution(job.getName(), jobParameters);
		firstExecution.setStartTime(new Date(0));
		firstExecution.setEndTime(new Date(1));
		getJobRepository().update(firstExecution);
		JobExecution secondExecution = getJobRepository().createJobExecution(job.getName(), jobParameters);

		assertEquals(firstExecution.getJobInstance(), secondExecution.getJobInstance());
		assertEquals(job.getName(), secondExecution.getJobInstance().getJobName());
	}

	/*
	 * Save multiple StepExecutions for the same step and check the returned
	 * count and last execution are correct.
	 */
	@Transactional
	@Test
	public void testGetStepExecutionCountAndLastStepExecution() throws Exception {
		job.setRestartable(true);
		StepSupport step = new StepSupport("restartedStep");

		// first execution
		JobExecution firstJobExec = getJobRepository().createJobExecution(job.getName(), jobParameters);
		StepExecution firstStepExec = new StepExecution(step.getName(), firstJobExec);
		getJobRepository().add(firstStepExec);

		assertEquals(1, getJobRepository().getStepExecutionCount(firstJobExec.getJobInstance(), step.getName()));
		assertEquals(firstStepExec, getJobRepository().getLastStepExecution(firstJobExec.getJobInstance(), step.getName()));

		// first execution failed
		firstJobExec.setStartTime(new Date(4));
		firstStepExec.setStartTime(new Date(5));
		firstStepExec.setStatus(BatchStatus.FAILED);
		firstStepExec.setEndTime(new Date(6));
		getJobRepository().update(firstStepExec);
		firstJobExec.setStatus(BatchStatus.FAILED);
		firstJobExec.setEndTime(new Date(7));
		getJobRepository().update(firstJobExec);

		// second execution
		JobExecution secondJobExec = getJobRepository().createJobExecution(job.getName(), jobParameters);
		StepExecution secondStepExec = new StepExecution(step.getName(), secondJobExec);
		getJobRepository().add(secondStepExec);

		assertEquals(2, getJobRepository().getStepExecutionCount(secondJobExec.getJobInstance(), step.getName()));
		assertEquals(secondStepExec, getJobRepository().getLastStepExecution(secondJobExec.getJobInstance(), step.getName()));
	}

	/*
	 * Save execution context and retrieve it.
	 */
	@Transactional
	@Test
	public void testSaveExecutionContext() throws Exception {
		@SuppressWarnings("serial")
		ExecutionContext ctx = new ExecutionContext() {
			{
				putLong("crashedPosition", 7);
			}
		};
		JobExecution jobExec = getJobRepository().createJobExecution(job.getName(), jobParameters);
		jobExec.setStartTime(new Date(0));
		jobExec.setExecutionContext(ctx);
		Step step = new StepSupport("step1");
		StepExecution stepExec = new StepExecution(step.getName(), jobExec);
		stepExec.setExecutionContext(ctx);
		
		getJobRepository().add(stepExec);

		StepExecution retrievedStepExec = getJobRepository().getLastStepExecution(jobExec.getJobInstance(), step.getName());
		assertEquals(stepExec, retrievedStepExec);
		assertEquals(ctx, retrievedStepExec.getExecutionContext());

		// JobExecution retrievedJobExec =
		// jobRepository.getLastJobExecution(jobExec.getJobInstance());
		// assertEquals(jobExec, retrievedJobExec);
		// assertEquals(ctx, retrievedJobExec.getExecutionContext());
	}

	/*
	 * If JobExecution is already running, exception will be thrown in attempt
	 * to create new execution.
	 */
	@Transactional
	@Test
	public void testOnlyOneJobExecutionAllowedRunning() throws Exception {
		job.setRestartable(true);
		getJobRepository().createJobExecution(job.getName(), jobParameters);

		try {
			getJobRepository().createJobExecution(job.getName(), jobParameters);
			fail();
		}
		catch (JobExecutionAlreadyRunningException e) {
			// expected
		}
	}

	@Transactional
	@Test
	public void testGetLastJobExecution() throws Exception {
		JobExecution jobExecution = getJobRepository().createJobExecution(job.getName(), jobParameters);
		jobExecution.setStatus(BatchStatus.FAILED);
		jobExecution.setEndTime(new Date());
		getJobRepository().update(jobExecution);
		Thread.sleep(10);
		jobExecution = getJobRepository().createJobExecution(job.getName(), jobParameters);
		StepExecution stepExecution = new StepExecution("step1", jobExecution);
		getJobRepository().add(stepExecution);
		jobExecution.addStepExecutions(Arrays.asList(stepExecution));
		assertEquals(jobExecution, getJobRepository().getLastJobExecution(job.getName(), jobParameters));
		assertEquals(stepExecution, jobExecution.getStepExecutions().iterator().next());
	}

	@Test(expected=JobExecutionAlreadyRunningException.class)
	public void throwJobExecutionAlreadyRunningExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		getJobRepository().createJobExecution(job.getName(), jobParameters);
		getJobRepository().createJobExecution(job.getName(), jobParameters);
	}

	@Test(expected=JobInstanceAlreadyCompleteException.class)
	public void throwJobInstanceAlreadyCompleteExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addLong("longValue", 123L, true);
		JobExecution jobExecution = getJobRepository().createJobExecution(job.getName(), builder.toJobParameters());
		jobExecution.setStatus(BatchStatus.COMPLETED);
		jobExecution.setEndTime(new Date(6));
		getJobRepository().update(jobExecution);
		getJobRepository().createJobExecution(job.getName(), builder.toJobParameters());
	}
}
