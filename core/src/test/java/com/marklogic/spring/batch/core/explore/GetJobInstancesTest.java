package com.marklogic.spring.batch.core.explore;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import com.marklogic.spring.batch.JobParametersTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.launch.NoSuchJobException;

import java.util.List;

public class GetJobInstancesTest extends AbstractJobRepositoryTest {

    private final String JOB_NAME = "testJob";
    private final String JOB_NAME_2 = JOB_NAME + "2";
    private final String JOB_NAME_3 = JOB_NAME + "3";
    
    @Before
    public void initialize() {
        initializeJobRepository();
    }

    @Test
    public void retrieveJobInstanceByIdTest() {
        JobInstance expectedJobInstance = getJobRepository().createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        JobInstance actualJobInstance = getJobExplorer().getJobInstance(expectedJobInstance.getId());
        assertTrue(expectedJobInstance.equals(actualJobInstance));
    }

    @Test
    public void getJobInstanceCountTest() throws NoSuchJobException {
        createJobInstances();
        assertEquals(3, getJobExplorer().getJobInstanceCount(JOB_NAME));
    }

    @Test(expected = NoSuchJobException.class)
    public void getJobInstanceCountNoJobException() throws NoSuchJobException {
        getJobExplorer().getJobInstanceCount("NoJobs");
    }

    @Test
    public void getJobInstancesTest() {
        createJobInstances();
        List<JobInstance> jobInstances = getJobExplorer().getJobInstances(JOB_NAME, 1, 2);
        assertEquals(2, jobInstances.size());
    }

    @Test
    public void findJobInstancesTest() {
        createJobInstances();
        List<JobInstance> jobInstances = getJobExplorer().findJobInstancesByJobName(JOB_NAME, 1, 2);
        assertEquals(2, jobInstances.size());
    }

    @Test
    public void getJobNamesTest() {
        createJobInstances();
        List<String> jobNames = getJobExplorer().getJobNames();
        assertEquals(3, jobNames.size());
        assertTrue(jobNames.get(0).equals(JOB_NAME));
        assertTrue(jobNames.get(1).equals(JOB_NAME_2));
        assertTrue(jobNames.get(2).equals(JOB_NAME_3));
    }

    private void createJobInstances() {
        getJobRepository().createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        getJobRepository().createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        getJobRepository().createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
        getJobRepository().createJobInstance(JOB_NAME_2, JobParametersTestUtils.getJobParameters());
        getJobRepository().createJobInstance(JOB_NAME_3, JobParametersTestUtils.getJobParameters());
    }

}
