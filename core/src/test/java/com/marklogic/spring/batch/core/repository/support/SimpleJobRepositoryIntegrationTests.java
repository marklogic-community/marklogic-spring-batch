package com.marklogic.spring.batch.core.repository.support;

import com.marklogic.spring.batch.core.job.JobSupport;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import com.marklogic.spring.batch.core.step.StepSupport;
import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

public class SimpleJobRepositoryIntegrationTests extends AbstractJobRepositoryTest {

    private JobSupport job = new JobSupport("SimpleJobRepositoryIntegrationTestsJob");

    private JobParameters jobParameters = new JobParameters();

    private JobRepository jobRepository;

    @Before
    public void initialize() {
        jobRepository = new SimpleJobRepository(
                new MarkLogicJobInstanceDao(getClient(), getBatchProperties()),
                new MarkLogicJobExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicStepExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicExecutionContextDao(getClient(), getBatchProperties())
        );
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

        JobExecution firstExecution = jobRepository.createJobExecution(job.getName(), jobParams);
        firstExecution.setStartTime(new Date());
        assertNotNull(firstExecution.getLastUpdated());

        assertEquals(job.getName(), firstExecution.getJobInstance().getJobName());

        jobRepository.update(firstExecution);
        firstExecution.setEndTime(new Date());
        jobRepository.update(firstExecution);
        JobExecution secondExecution = jobRepository.createJobExecution(job.getName(), jobParams);

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

        JobExecution firstExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        firstExecution.setStartTime(new Date(0));
        firstExecution.setEndTime(new Date(1));
        jobRepository.update(firstExecution);
        JobExecution secondExecution = jobRepository.createJobExecution(job.getName(), jobParameters);

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
        JobExecution firstJobExec = jobRepository.createJobExecution(job.getName(), jobParameters);
        StepExecution firstStepExec = new StepExecution(step.getName(), firstJobExec);
        jobRepository.add(firstStepExec);

        assertEquals(1, jobRepository.getStepExecutionCount(firstJobExec.getJobInstance(), step.getName()));
        assertEquals(firstStepExec, jobRepository.getLastStepExecution(firstJobExec.getJobInstance(), step.getName()));

        // first execution failed
        firstJobExec.setStartTime(new Date(4));
        firstStepExec.setStartTime(new Date(5));
        firstStepExec.setStatus(BatchStatus.FAILED);
        firstStepExec.setEndTime(new Date(6));
        jobRepository.update(firstStepExec);
        firstJobExec.setStatus(BatchStatus.FAILED);
        firstJobExec.setEndTime(new Date(7));
        jobRepository.update(firstJobExec);

        // second execution
        JobExecution secondJobExec = jobRepository.createJobExecution(job.getName(), jobParameters);
        StepExecution secondStepExec = new StepExecution(step.getName(), secondJobExec);
        jobRepository.add(secondStepExec);

        assertEquals(2, jobRepository.getStepExecutionCount(secondJobExec.getJobInstance(), step.getName()));
        assertEquals(secondStepExec, jobRepository.getLastStepExecution(secondJobExec.getJobInstance(), step.getName()));
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
        JobExecution jobExec = jobRepository.createJobExecution(job.getName(), jobParameters);
        jobExec.setStartTime(new Date(0));
        jobExec.setExecutionContext(ctx);
        Step step = new StepSupport("step1");
        StepExecution stepExec = new StepExecution(step.getName(), jobExec);
        stepExec.setExecutionContext(ctx);

        jobRepository.add(stepExec);

        StepExecution retrievedStepExec = jobRepository.getLastStepExecution(jobExec.getJobInstance(), step.getName());
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
        jobRepository.createJobExecution(job.getName(), jobParameters);

        try {
            jobRepository.createJobExecution(job.getName(), jobParameters);
            fail();
        } catch (JobExecutionAlreadyRunningException e) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testGetLastJobExecution() throws Exception {
        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        jobExecution.setStatus(BatchStatus.FAILED);
        jobExecution.setEndTime(new Date());
        jobRepository.update(jobExecution);
        Thread.sleep(10);
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        StepExecution stepExecution = new StepExecution("step1", jobExecution);
        jobRepository.add(stepExecution);
        jobExecution.addStepExecutions(Arrays.asList(stepExecution));
        assertEquals(jobExecution, jobRepository.getLastJobExecution(job.getName(), jobParameters));
        assertEquals(stepExecution, jobExecution.getStepExecutions().iterator().next());
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void throwJobExecutionAlreadyRunningExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobRepository.createJobExecution(job.getName(), jobParameters);
        jobRepository.createJobExecution(job.getName(), jobParameters);
    }

    @Test(expected = JobInstanceAlreadyCompleteException.class)
    public void throwJobInstanceAlreadyCompleteExceptionTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong("longValue", 123L, true);
        JobExecution jobExecution = jobRepository.createJobExecution(job.getName(), builder.toJobParameters());
        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobExecution.setEndTime(new Date(6));
        jobRepository.update(jobExecution);
        jobRepository.createJobExecution(job.getName(), builder.toJobParameters());
    }
}
