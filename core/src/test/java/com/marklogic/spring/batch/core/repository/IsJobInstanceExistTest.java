package com.marklogic.spring.batch.core.repository;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

public class IsJobInstanceExistTest extends AbstractJobRepositoryTest {

    private final String jobName = "job";
    private JobParameters params;

    @Before
    public void JobExecution() throws JobExecutionAlreadyRunningException, JobRestartException,
        JobInstanceAlreadyCompleteException {
        initializeJobRepository();
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong("count", 123L);
        getJobRepository().createJobExecution(jobName, builder.toJobParameters());
        params = builder.toJobParameters();
    }

    @Test
    public void verifyJobInstanceExistsTest() {
        assertTrue(getJobRepository().isJobInstanceExists(jobName, params));
    }

    @Test
    public void verifyJobInstanceDoesNotExistWithJobNameTest() {
        assertFalse(getJobRepository().isJobInstanceExists(jobName + "-test", params));
    }

    @Test
    public void verifyJobInstanceDoesNotExistWithJobParametersTest() {
        JobParametersBuilder builder = new JobParametersBuilder(params);
        builder.addLong("second", 100L, true);
        assertFalse(getJobRepository().isJobInstanceExists(jobName, builder.toJobParameters()));
    }

}
