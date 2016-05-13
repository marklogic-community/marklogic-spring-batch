package com.marklogic.spring.batch.core.explore.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.core.job.JobSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
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
    private JobExecutionDao jobExecutionDao;

    @Autowired
    private JobInstanceDao jobInstanceDao;

    @Autowired
    private StepExecutionDao stepExecutionDao;

    private JobSupport job = new JobSupport("SimpleJobExplorerTestsJob");

    @Autowired
    private ExecutionContextDao ecDao;

    private JobExecution jobExecution;
    private JobInstance jobInstance;

    @Before
    public void createJobExecution() {
        JobParameters jobParameters = new JobParameters();
        jobInstance = jobInstanceDao.createJobInstance(job.getName(), jobParameters);
        jobExecution = new JobExecution(jobInstance, 123L, jobParameters, null);
        jobExecutionDao.saveJobExecution(jobExecution);
    }

    @Test
    public void testGetJobExecution() throws Exception {
        stepExecutionDao.addStepExecutions(jobExecution);
        jobExplorer.getJobExecution(jobExecution.getId());
    }

    @Test
    public void testMissingGetJobExecution() throws Exception {
        when(jobExecutionDao.getJobExecution(123L)).thenReturn(null);
        assertNull(jobExplorer.getJobExecution(123L));
    }

    @Test
    public void testGetStepExecution() throws Exception {
        when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(jobExecution);
        when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(jobInstance);
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        when(stepExecutionDao.getStepExecution(jobExecution, 123L))
                .thenReturn(stepExecution);
        when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        stepExecution = jobExplorer.getStepExecution(jobExecution.getId(), 123L);

        assertEquals(jobInstance,
                stepExecution.getJobExecution().getJobInstance());

        verify(jobInstanceDao).getJobInstance(jobExecution);
    }

    @Test
    public void testGetStepExecutionMissing() throws Exception {
        when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(jobExecution);
        when(stepExecutionDao.getStepExecution(jobExecution, 123L))
                .thenReturn(null);
        assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testGetStepExecutionMissingJobExecution() throws Exception {
        when(jobExecutionDao.getJobExecution(jobExecution.getId())).thenReturn(null);
        assertNull(jobExplorer.getStepExecution(jobExecution.getId(), 123L));
    }

    @Test
    public void testFindRunningJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        when(jobExecutionDao.findRunningJobExecutions("job")).thenReturn(
                Collections.singleton(jobExecution));
        when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(
                jobInstance);
        stepExecutionDao.addStepExecutions(jobExecution);
        when(ecDao.getExecutionContext(jobExecution)).thenReturn(null);
        when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        jobExplorer.findRunningJobExecutions("job");
    }

    @Test
    public void testFindJobExecutions() throws Exception {
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        when(jobExecutionDao.findJobExecutions(jobInstance)).thenReturn(
                Collections.singletonList(jobExecution));
        when(jobInstanceDao.getJobInstance(jobExecution)).thenReturn(
                jobInstance);
        stepExecutionDao.addStepExecutions(jobExecution);
        when(ecDao.getExecutionContext(jobExecution)).thenReturn(null);
        when(ecDao.getExecutionContext(stepExecution)).thenReturn(null);
        jobExplorer.getJobExecutions(jobInstance);
    }

    @Test
    public void testGetJobInstance() throws Exception {
        jobInstanceDao.getJobInstance(111L);
        jobExplorer.getJobInstance(111L);
    }

    @Test
    public void testGetLastJobInstances() throws Exception {
        jobInstanceDao.getJobInstances("foo", 0, 1);
        jobExplorer.getJobInstances("foo", 0, 1);
    }

    @Test
    public void testGetJobNames() throws Exception {
        jobInstanceDao.getJobNames();
        jobExplorer.getJobNames();
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        when(jobInstanceDao.getJobInstanceCount("myJob")).thenReturn(4);

        assertEquals(4, jobExplorer.getJobInstanceCount("myJob"));
    }

    @Test(expected=NoSuchJobException.class)
    public void testGetJobInstanceCountException() throws Exception {
        when(jobInstanceDao.getJobInstanceCount("throwException")).thenThrow(new NoSuchJobException("expected"));

        jobExplorer.getJobInstanceCount("throwException");
    }
}