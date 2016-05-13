package com.marklogic.spring.batch.core.explore.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
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
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
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

    @Autowired
    private ExecutionContextDao ecDao;

    private JobExecution jobExecution;
    private JobInstance jobInstance;
    private StepExecution stepExecution;

    @Before
    public void createJobExecution() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("stringKey", "stringValue").addLong("longKey", 1L).addDouble("doubleKey", 1.1).addDate(
                "dateKey", new Date(1L));
        JobParameters jobParams = builder.toJobParameters();

        jobExecution = jobRepository.createJobExecution(job.getName(), jobParams);
        StepExecution se = new StepExecution("stepTest", jobExecution);
        jobRepository.add(se);
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

        assertEquals(jobInstance,
                stepExecution.getJobExecution().getJobInstance());

    }

    @Test
    public void testGetStepExecutionMissing() throws Exception {
        assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testGetStepExecutionMissingJobExecution() throws Exception {
        assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testFindRunningJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        Set<JobExecution> je = jobExplorer.findRunningJobExecutions(job.getName());
        assertTrue(je.size() > 0);

    }

    @Test
    public void testFindJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        when(ecDao.getExecutionContext(jobExecution)).thenReturn(null);
        when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        jobExplorer.getJobExecutions(jobInstance);
    }

    @Test
    public void testGetJobInstance() throws Exception {
        jobExplorer.getJobInstance(111L);
    }

    @Test
    public void testGetLastJobInstances() throws Exception {
        jobExplorer.getJobInstances("foo", 0, 1);
    }

    @Test
    public void testGetJobNames() throws Exception {
        jobExplorer.getJobNames();
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        assertEquals(4, jobExplorer.getJobInstanceCount("myJob"));
    }

    @Test(expected=NoSuchJobException.class)
    public void testGetJobInstanceCountException() throws Exception {
        jobExplorer.getJobInstanceCount("throwException");
    }
}