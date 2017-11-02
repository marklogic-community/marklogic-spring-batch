package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.NoSuchObjectException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class MarkLogicJobDaoTests extends AbstractJobRepositoryTest {

    protected JobParameters jobParameters = new JobParametersBuilder().addString("job.key", "jobKey").addLong("long",
            (long) 1).addDate("date", new Date(7)).addDouble("double", 7.7).toJobParameters();

    protected JobInstance jobInstance;

    protected String jobName = "Job1";

    protected JobExecution jobExecution;

    protected JobInstanceDao jobInstanceDao;
    protected JobExecutionDao jobExecutionDao;

    protected Date jobExecutionStartTime = new Date(System.currentTimeMillis());

    @Before
    public void onSetUpInTransaction() throws Exception {
        jobInstanceDao = new MarkLogicJobInstanceDao(getClient(), getBatchProperties());
        jobExecutionDao = new MarkLogicJobExecutionDao(getClient(), getBatchProperties());
        // Create job.
        jobInstance = jobInstanceDao.createJobInstance(jobName, jobParameters);

        // Create an execution
        jobExecutionStartTime = new Date(System.currentTimeMillis());
        jobExecution = new JobExecution(jobInstance, jobParameters);
        jobExecution.setStartTime(jobExecutionStartTime);
        jobExecution.setStatus(BatchStatus.STARTED);
        jobExecutionDao.saveJobExecution(jobExecution);
    }

    @Transactional
    @Test
    public void testVersionIsNotNullForJob() throws Exception {
        assertEquals(0, jobInstanceDao.getJobInstance(jobInstance.getId()).getVersion().intValue());
    }

    @Transactional
    @Test
    public void testVersionIsNotNullForJobExecution() throws Exception {
        int version = jobExecutionDao.getJobExecution(jobExecution.getId()).getVersion().intValue();
        assertTrue((Integer.MIN_VALUE <= version) && (version <= Integer.MAX_VALUE));
    }

    @Transactional
    @Test
    public void testFindNonExistentJob() {
        // No job should be found since it hasn't been created.
        JobInstance jobInstance = jobInstanceDao.getJobInstance("nonexistentJob", jobParameters);
        assertNull(jobInstance);
    }

    @Transactional
    @Test
    public void testFindJob() {
        JobInstance instance = jobInstanceDao.getJobInstance(jobName, jobParameters);
        assertNotNull(instance);
        assertTrue(jobInstance.equals(instance));
    }

    @Transactional
    @Test
    public void testFindJobWithNullRuntime() {

        try {
            jobInstanceDao.getJobInstance(null, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Test that ensures that if you create a job with a given name, then find a
     * job with the same name, but other pieces of the identifier different, you
     * get no result, not the existing one.
     */
    @Transactional
    @Test
    public void testCreateJobWithExistingName() {

        String scheduledJob = "ScheduledJob";
        jobInstanceDao.createJobInstance(scheduledJob, jobParameters);

        // Modifying the key should bring back a completely different
        // JobInstance
        JobParameters tempProps = new JobParametersBuilder().addString("job.key", "testKey1").toJobParameters();

        JobInstance instance;
        instance = jobInstanceDao.getJobInstance(scheduledJob, jobParameters);
        assertNotNull(instance);

        instance = jobInstanceDao.getJobInstance(scheduledJob, tempProps);
        assertNull(instance);

    }

    @Transactional
    @Test
    public void testUpdateJobExecution() {

        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobExecution.setExitStatus(ExitStatus.COMPLETED);
        jobExecution.setEndTime(new Date(System.currentTimeMillis()));
        jobExecutionDao.updateJobExecution(jobExecution);

        List<JobExecution> executions = jobExecutionDao.findJobExecutions(jobInstance);
        assertEquals(executions.size(), 1);
        validateJobExecution(jobExecution, executions.get(0));

    }

    @Transactional
    @Test
    public void testSaveJobExecution() {

        List<JobExecution> executions = jobExecutionDao.findJobExecutions(jobInstance);
        assertEquals(executions.size(), 1);
        validateJobExecution(jobExecution, executions.get(0));
    }

    @Transactional
    @Test
    public void testUpdateInvalidJobExecution() {

        // id is invalid
        JobExecution execution = new JobExecution(jobInstance, (long) 29432, jobParameters, null);
        execution.incrementVersion();
        try {
            jobExecutionDao.updateJobExecution(execution);
            fail("Expected NoSuchBatchDomainObjectException");
        } catch (NoSuchObjectException ex) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testUpdateNullIdJobExection() {

        JobExecution execution = new JobExecution(jobInstance, jobParameters);
        try {
            jobExecutionDao.updateJobExecution(execution);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }


    @Transactional
    @Test
    public void testJobWithSimpleJobIdentifier() throws Exception {

        String testJob = "test";
        // Create job.
        jobInstance = jobInstanceDao.createJobInstance(testJob, jobParameters);

        JobInstance jobInst = jobInstanceDao.getJobInstance(jobInstance.getId());

        assertNotNull(jobInst);
        assertEquals("test", jobInst.getJobName());

    }

    @Transactional
    @Test
    public void testJobWithDefaultJobIdentifier() throws Exception {

        String testDefaultJob = "testDefault";
        // Create job.
        jobInstance = jobInstanceDao.createJobInstance(testDefaultJob, jobParameters);

        JobInstance instance = jobInstanceDao.getJobInstance(testDefaultJob, jobParameters);

        assertNotNull(instance);
    }

    @Transactional
    @Test
    public void testFindJobExecutions() {

        List<JobExecution> results = jobExecutionDao.findJobExecutions(jobInstance);
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
        jobExecutionDao.saveJobExecution(lastExecution);

        assertEquals(lastExecution, jobExecutionDao.getLastJobExecution(jobInstance));
        assertNotNull(lastExecution.getJobParameters());
        assertEquals("jobKey", lastExecution.getJobParameters().getString("job.key"));
    }

    /**
     * Trying to create instance twice for the same job+parameters causes error
     */
    @Transactional
    @Test
    public void testCreateDuplicateInstance() {

        jobParameters = new JobParameters();

        jobInstanceDao.createJobInstance(jobName, jobParameters);

        try {
            jobInstanceDao.createJobInstance(jobName, jobParameters);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testCreationAddsVersion() {

        jobInstance = jobInstanceDao.createJobInstance("testCreationAddsVersion", new JobParameters());

        assertNotNull(jobInstance.getVersion());
    }

    @Transactional
    @Test
    public void testSaveAddsVersionAndId() {

        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);

        assertNull(jobExecution.getId());
        assertNull(jobExecution.getVersion());

        jobExecutionDao.saveJobExecution(jobExecution);

        assertNotNull(jobExecution.getId());
        assertNotNull(jobExecution.getVersion());
    }

    @Transactional
    @Test
    public void testUpdateIncrementsVersion() {
        int version = jobExecution.getVersion();

        jobExecutionDao.updateJobExecution(jobExecution);

        assertNotEquals(version, jobExecution.getVersion().intValue());
    }
}