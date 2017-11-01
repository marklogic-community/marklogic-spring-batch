package com.marklogic.spring.batch.core.explore;

import com.marklogic.spring.batch.JobParametersTestUtils;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.SimpleJobRepository;

import java.util.List;

public class GetJobInstancesTest extends AbstractJobRepositoryTest {

    private final String JOB_NAME = "testJob";
    private final String JOB_NAME_2 = JOB_NAME + "2";
    private final String JOB_NAME_3 = JOB_NAME + "3";

    private JobRepository jobRepository;
    private JobExplorer jobExplorer;

    @Before
    public void initialize() {
        jobRepository = new SimpleJobRepository(
                new MarkLogicJobInstanceDao(getClient(), getBatchProperties()),
                new MarkLogicJobExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicStepExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicExecutionContextDao(getClient(), getBatchProperties())
        );
        jobExplorer = new SimpleJobExplorer(
                new MarkLogicJobInstanceDao(getClient(), getBatchProperties()),
                new MarkLogicJobExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicStepExecutionDao(getClient(), getBatchProperties()),
                new MarkLogicExecutionContextDao(getClient(), getBatchProperties())
        );
    }

    @Test
    public void retrieveJobInstanceByIdTest() {
        JobInstance expectedJobInstance = jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        JobInstance actualJobInstance = jobExplorer.getJobInstance(expectedJobInstance.getId());
        assertTrue(expectedJobInstance.equals(actualJobInstance));
    }

    @Test
    public void getJobInstanceCountTest() throws NoSuchJobException {
        createJobInstances();
        assertEquals(3, jobExplorer.getJobInstanceCount(JOB_NAME));
    }

    @Test(expected = NoSuchJobException.class)
    public void getJobInstanceCountNoJobException() throws NoSuchJobException {
        jobExplorer.getJobInstanceCount("NoJobs");
    }

    @Test
    public void getJobInstancesTest() {
        createJobInstances();
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(JOB_NAME, 1, 2);
        assertEquals(2, jobInstances.size());
    }

    @Test
    public void findJobInstancesTest() {
        createJobInstances();
        List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(JOB_NAME, 1, 2);
        assertEquals(2, jobInstances.size());
    }

    @Test
    public void getJobNamesTest() {
        createJobInstances();
        List<String> jobNames = jobExplorer.getJobNames();
        assertEquals(3, jobNames.size());
        assertTrue(jobNames.get(0).equals(JOB_NAME));
        assertTrue(jobNames.get(1).equals(JOB_NAME_2));
        assertTrue(jobNames.get(2).equals(JOB_NAME_3));
    }

    private void createJobInstances() {
        jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        jobRepository.createJobInstance(JOB_NAME_2, JobParametersTestUtils.getJobParameters());
        jobRepository.createJobInstance(JOB_NAME_3, JobParametersTestUtils.getJobParameters());
    }

}
