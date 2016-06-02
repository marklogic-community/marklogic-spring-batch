package com.marklogic.spring.batch.core.explore.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.core.job.JobSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { com.marklogic.spring.batch.core.repository.dao.MarkLogicDaoConfig.class, com.marklogic.junit.spring.BasicTestConfig.class })
public class SimpleJobExplorerTests extends AbstractSpringTest {

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    private JobSupport job = new JobSupport("SimpleJobExplorerTestsJob");

    private JobExecution jobExecution;
    private JobInstance jobInstance;
    private StepExecution stepExecution;
    private JobParametersBuilder builder = new JobParametersBuilder();

    @Before
    public void createJobExecution() throws Exception {
        builder.addString("stringKey", "stringValue").addLong("longKey", 1L).addDouble("doubleKey", 1.1).addDate(
                "dateKey", new Date(1L));
        JobParameters jobParams = builder.toJobParameters();
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParams);
        jobInstance = jobExecution.getJobInstance();
        stepExecution = new StepExecution("stepTest", jobExecution);
        jobRepository.add(stepExecution);
    }

    @Test
    public void testGetJobExecution() throws Exception {
        JobExecution je = jobExplorer.getJobExecution(jobExecution.getId());
        assertNotNull(je);
        assertEquals(jobExecution, je);
    }

    @Test
    public void testMissingGetJobExecution() throws Exception {
        assertNull(jobExplorer.getJobExecution(123L));
    }

    @Test
    public void testGetStepExecution() throws Exception {
        StepExecution se = jobExplorer.getStepExecution(jobExecution.getId(), stepExecution.getId());
        assertNotNull(se);
        assertEquals(jobInstance,
                stepExecution.getJobExecution().getJobInstance());

    }

    @Test
    public void testGetStepExecutionMissing() throws Exception {
        assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testGetStepExecutionMissingJobExecution() throws Exception {
        assertNull(jobExplorer.getStepExecution(123L, stepExecution.getId()));
    }

    @Test
    public void testFindRunningJobExecutions() throws Exception {
        Set<JobExecution> je = jobExplorer.findRunningJobExecutions(job.getName());
        assertTrue(je.size() == 1);

        jobExecution.setEndTime(new Date(6));
        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobRepository.update(jobExecution);

        je = jobExplorer.findRunningJobExecutions(job.getName());
        assertTrue(je.size() == 0);
    }

    @Test
    public void testFindJobExecutions() throws Exception {
        List<JobExecution> je = jobExplorer.getJobExecutions(jobInstance);
        assertTrue(je.size() == 1);
        builder.addLong("test", 123L, true);
        jobRepository.createJobExecution(jobInstance, builder.toJobParameters(), null);
        je = jobExplorer.getJobExecutions(jobInstance);
        assertTrue(je.size() == 2);

    }

    @Test
    public void testGetJobInstance() throws Exception {
        assertEquals(jobInstance, jobExplorer.getJobInstance(jobInstance.getId()));
    }

    @Test
    public void testGetLastJobInstances() throws Exception {

        jobExplorer.getJobInstances("foo", 0, 1);
    }

    @Test
    public void testGetJobNames() throws Exception {
        List<String> jobNames = jobExplorer.getJobNames();
        assertTrue(jobNames.get(0).equals(job.getName()));
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        builder.addLong("long1", 123L, true);
        jobRepository.createJobInstance(job.getName(), builder.toJobParameters());
        builder.addLong("long1", 124L, true);
        jobRepository.createJobInstance(job.getName(), builder.toJobParameters());
        assertEquals(3, jobExplorer.getJobInstanceCount(job.getName()));
    }

    @Test(expected=NoSuchJobException.class)
    public void testGetJobInstanceCountException() throws Exception {
        jobExplorer.getJobInstanceCount("throwException");
    }
}