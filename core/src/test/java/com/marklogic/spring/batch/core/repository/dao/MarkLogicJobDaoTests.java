package com.marklogic.spring.batch.core.repository.dao;

import java.util.Date;
import java.util.List;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.dao.NoSuchObjectException;
import org.springframework.transaction.annotation.Transactional;

public class MarkLogicJobDaoTests extends AbstractJobRepositoryTest {
	
	protected JobParameters jobParameters = new JobParametersBuilder().addString("job.key", "jobKey").addLong("long",
			(long) 1).addDate("date", new Date(7)).addDouble("double", 7.7).toJobParameters();

	protected JobInstance jobInstance;

	protected String jobName = "Job1";

	protected JobExecution jobExecution;

	protected Date jobExecutionStartTime = new Date(System.currentTimeMillis());
	
	@Before
	public void onSetUpInTransaction() throws Exception {
		initializeJobRepository();
		
		// Create job.
		jobInstance = getJobInstanceDao().createJobInstance(jobName, jobParameters);

		// Create an execution
		jobExecutionStartTime = new Date(System.currentTimeMillis());
		jobExecution = new JobExecution(jobInstance, jobParameters);
		jobExecution.setStartTime(jobExecutionStartTime);
		jobExecution.setStatus(BatchStatus.STARTED);
		getJobExecutionDao().saveJobExecution(jobExecution);
	}

	@Transactional @Test
	public void testVersionIsNotNullForJob() throws Exception {
		assertEquals(0, getJobInstanceDao().getJobInstance(jobInstance.getId()).getVersion().intValue());
	}

	@Transactional @Test
	public void testVersionIsNotNullForJobExecution() throws Exception {
		assertEquals(0, getJobExecutionDao().getJobExecution(jobExecution.getId()).getVersion().intValue());
	}

	@Transactional @Test
	public void testFindNonExistentJob() {
		// No job should be found since it hasn't been created.
		JobInstance jobInstance = getJobInstanceDao().getJobInstance("nonexistentJob", jobParameters);
		assertNull(jobInstance);
	}

	@Transactional @Test
	public void testFindJob() {
		JobInstance instance = getJobInstanceDao().getJobInstance(jobName, jobParameters);
		assertNotNull(instance);
		assertTrue(jobInstance.equals(instance));
	}

	@Transactional @Test
	public void testFindJobWithNullRuntime() {

		try {
			getJobInstanceDao().getJobInstance(null, null);
			fail();
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	/**
	 * Test that ensures that if you create a job with a given name, then find a
	 * job with the same name, but other pieces of the identifier different, you
	 * get no result, not the existing one.
	 */
	@Transactional @Test
	public void testCreateJobWithExistingName() {

		String scheduledJob = "ScheduledJob";
		getJobInstanceDao().createJobInstance(scheduledJob, jobParameters);

		// Modifying the key should bring back a completely different
		// JobInstance
		JobParameters tempProps = new JobParametersBuilder().addString("job.key", "testKey1").toJobParameters();

		JobInstance instance;
		instance = getJobInstanceDao().getJobInstance(scheduledJob, jobParameters);
		assertNotNull(instance);

		instance = getJobInstanceDao().getJobInstance(scheduledJob, tempProps);
		assertNull(instance);

	}

	@Transactional @Test
	public void testUpdateJobExecution() {

		jobExecution.setStatus(BatchStatus.COMPLETED);
		jobExecution.setExitStatus(ExitStatus.COMPLETED);
		jobExecution.setEndTime(new Date(System.currentTimeMillis()));
		getJobExecutionDao().updateJobExecution(jobExecution);

		List<JobExecution> executions = getJobExecutionDao().findJobExecutions(jobInstance);
		assertEquals(executions.size(), 1);
		validateJobExecution(jobExecution, executions.get(0));

	}

	@Transactional @Test
	public void testSaveJobExecution() {

		List<JobExecution> executions = getJobExecutionDao().findJobExecutions(jobInstance);
		assertEquals(executions.size(), 1);
		validateJobExecution(jobExecution, executions.get(0));
	}

	@Transactional @Test
	public void testUpdateInvalidJobExecution() {

		// id is invalid
		JobExecution execution = new JobExecution(jobInstance, (long) 29432, jobParameters, null);
		execution.incrementVersion();
		try {
			getJobExecutionDao().updateJobExecution(execution);
			fail("Expected NoSuchBatchDomainObjectException");
		}
		catch (NoSuchObjectException ex) {
			// expected
		}
	}

	@Transactional @Test
	public void testUpdateNullIdJobExection() {

		JobExecution execution = new JobExecution(jobInstance, jobParameters);
		try {
			getJobExecutionDao().updateJobExecution(execution);
			fail();
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}


	@Transactional @Test
	public void testJobWithSimpleJobIdentifier() throws Exception {

		String testJob = "test";
		// Create job.
		jobInstance = getJobInstanceDao().createJobInstance(testJob, jobParameters);

		JobInstance jobInst = getJobInstanceDao().getJobInstance(jobInstance.getId());
		
		assertNotNull(jobInst);
		assertEquals("test", jobInst.getJobName());

	}

	@Transactional @Test
	public void testJobWithDefaultJobIdentifier() throws Exception {

		String testDefaultJob = "testDefault";
		// Create job.
		jobInstance = getJobInstanceDao().createJobInstance(testDefaultJob, jobParameters);

		JobInstance instance = getJobInstanceDao().getJobInstance(testDefaultJob, jobParameters);

		assertNotNull(instance);
	}

	@Transactional @Test
	public void testFindJobExecutions() {

		List<JobExecution> results = getJobExecutionDao().findJobExecutions(jobInstance);
		assertEquals(results.size(), 1);
		validateJobExecution(jobExecution, results.get(0));
	}

	private void validateJobExecution(JobExecution lhs, JobExecution rhs) {

		// equals operator only checks id
		assertEquals(lhs, rhs);
		assertEquals(lhs.getStartTime(), rhs.getStartTime());
		assertEquals(lhs.getEndTime(), rhs.getEndTime());
		assertEquals(lhs.getStatus(), rhs.getStatus());
		assertEquals(lhs.getExitStatus(), rhs.getExitStatus());
	}

	@Transactional 
	@Test
	public void testGetLastJobExecution() {
		JobExecution lastExecution = new JobExecution(jobInstance, jobParameters);
		lastExecution.setStatus(BatchStatus.STARTED);

		int JUMP_INTO_FUTURE = 1000; // makes sure start time is 'greatest'
		lastExecution.setCreateTime(new Date(System.currentTimeMillis() + JUMP_INTO_FUTURE));
		getJobExecutionDao().saveJobExecution(lastExecution);

		assertEquals(lastExecution, getJobExecutionDao().getLastJobExecution(jobInstance));
		assertNotNull(lastExecution.getJobParameters());
		assertEquals("jobKey", lastExecution.getJobParameters().getString("job.key"));
	}

	/**
	 * Trying to create instance twice for the same job+parameters causes error
	 */
	@Transactional @Test
	public void testCreateDuplicateInstance() {

		jobParameters = new JobParameters();
		
		getJobInstanceDao().createJobInstance(jobName, jobParameters);

		try {
			getJobInstanceDao().createJobInstance(jobName, jobParameters);
			fail();
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	@Transactional @Test
	public void testCreationAddsVersion() {

		jobInstance = getJobInstanceDao().createJobInstance("testCreationAddsVersion", new JobParameters());

		assertNotNull(jobInstance.getVersion());
	}

	@Transactional @Test
	public void testSaveAddsVersionAndId() {

		JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);

		assertNull(jobExecution.getId());
		assertNull(jobExecution.getVersion());

		getJobExecutionDao().saveJobExecution(jobExecution);

		assertNotNull(jobExecution.getId());
		assertNotNull(jobExecution.getVersion());
	}

	@Transactional @Test
	public void testUpdateIncrementsVersion() {
		int version = jobExecution.getVersion();

		getJobExecutionDao().updateJobExecution(jobExecution);

		assertEquals(version + 1, jobExecution.getVersion().intValue());
	}
}