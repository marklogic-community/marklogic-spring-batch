package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.spring.batch.core.step.StepSupport;
import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MarkLogicStepExecutionsDaoTest extends AbstractJobRepositoryTest {

    protected JobInstance jobInstance;
    protected JobExecution jobExecution;
    protected Step step;
    protected StepExecution stepExecution;
    protected StepExecutionDao dao;

    @Before
    public void onSetUp() throws Exception {
        JobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao(getClient(), getBatchProperties());
        JobInstance jobInstance = jobInstanceDao.createJobInstance("myJob", new JobParameters());

        JobExecutionDao jobExecutionDao = new MarkLogicJobExecutionDao(getClient(), getBatchProperties());
        jobExecution = new JobExecution(jobInstance, new JobParameters());
        jobExecutionDao.saveJobExecution(jobExecution);

        step = new StepSupport("foo");
        stepExecution = new StepExecution(step.getName(), jobExecution);

        dao = new MarkLogicStepExecutionDao(getClient(), getBatchProperties());
    }

    @Transactional
    @Test
    public void testSaveExecutionAssignsIdAndVersion() throws Exception {

        assertNull(stepExecution.getId());
        assertNull(stepExecution.getVersion());
        dao.saveStepExecution(stepExecution);
        assertNotNull(stepExecution.getId());
        assertNotNull(stepExecution.getVersion());
    }

    @Transactional
    @Test
    public void testSaveAndGetExecution() {

        stepExecution.setStatus(BatchStatus.STARTED);
        stepExecution.setReadSkipCount(7);
        stepExecution.setProcessSkipCount(2);
        stepExecution.setWriteSkipCount(5);
        stepExecution.setProcessSkipCount(11);
        stepExecution.setRollbackCount(3);
        stepExecution.setLastUpdated(new Date(System.currentTimeMillis()));
        stepExecution.setReadCount(17);
        stepExecution.setFilterCount(15);
        stepExecution.setWriteCount(13);
        dao.saveStepExecution(stepExecution);

        StepExecution retrieved = dao.getStepExecution(jobExecution, stepExecution.getId());

        assertStepExecutionsAreEqual(stepExecution, retrieved);
        assertNotNull(retrieved.getVersion());
        assertNotNull(retrieved.getJobExecution());
        assertNotNull(retrieved.getJobExecution().getId());
        assertNotNull(retrieved.getJobExecution().getJobId());
        assertNotNull(retrieved.getJobExecution().getJobInstance());

    }

    @Transactional
    @Test
    public void testSaveAndGetExecutions() {

        List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
        for (int i = 0; i < 3; i++) {
            StepExecution se = new StepExecution("step" + i, jobExecution);
            se.setStatus(BatchStatus.STARTED);
            se.setReadSkipCount(i);
            se.setProcessSkipCount(i);
            se.setWriteSkipCount(i);
            se.setProcessSkipCount(i);
            se.setRollbackCount(i);
            se.setLastUpdated(new Date(System.currentTimeMillis()));
            se.setReadCount(i);
            se.setFilterCount(i);
            se.setWriteCount(i);
            stepExecutions.add(se);
        }

        dao.saveStepExecutions(stepExecutions);

        for (int i = 0; i < 3; i++) {

            StepExecution retrieved = dao.getStepExecution(jobExecution, stepExecutions.get(i).getId());

            assertStepExecutionsAreEqual(stepExecutions.get(i), retrieved);
            assertNotNull(retrieved.getVersion());
            assertNotNull(retrieved.getJobExecution());
            assertNotNull(retrieved.getJobExecution().getId());
            assertNotNull(retrieved.getJobExecution().getJobId());
            assertNotNull(retrieved.getJobExecution().getJobInstance());
        }
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testSaveNullCollectionThrowsException() {
        dao.saveStepExecutions(null);
    }

    @Transactional
    @Test
    public void testSaveEmptyCollection() {
        dao.saveStepExecutions(new ArrayList<StepExecution>());
    }

    @Transactional
    @Test
    public void testSaveAndGetNonExistentExecution() {
        assertNull(dao.getStepExecution(jobExecution, 45677L));
    }

    @Transactional
    @Test
    public void testSaveAndFindExecution() {

        stepExecution.setStatus(BatchStatus.STARTED);
        stepExecution.setReadSkipCount(7);
        stepExecution.setWriteSkipCount(5);
        stepExecution.setRollbackCount(3);
        dao.saveStepExecution(stepExecution);

        dao.addStepExecutions(jobExecution);
        Collection<StepExecution> retrieved = jobExecution.getStepExecutions();
        assertStepExecutionsAreEqual(stepExecution, retrieved.iterator().next());
    }

    @Transactional
    @Test
    public void testGetForNotExistingJobExecution() {
        assertNull(dao.getStepExecution(jobExecution, 11L));
    }

    /**
     * To-be-saved execution must not already have an id.
     */
    @Transactional
    @Test
    public void testSaveExecutionWithIdAlreadySet() {
        stepExecution.setId((long) 7);
        try {
            dao.saveStepExecution(stepExecution);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * To-be-saved execution must not already have a version.
     */
    @Transactional
    @Test
    public void testSaveExecutionWithVersionAlreadySet() {
        stepExecution.incrementVersion();
        try {
            dao.saveStepExecution(stepExecution);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Update and retrieve updated StepExecution - make sure the update is
     * reflected as expected and version number has been incremented
     */
    @Transactional
    @Test
    public void testUpdateExecution() {
        stepExecution.setStatus(BatchStatus.STARTED);
        dao.saveStepExecution(stepExecution);
        Integer versionAfterSave = stepExecution.getVersion();

        stepExecution.setStatus(BatchStatus.ABANDONED);
        stepExecution.setLastUpdated(new Date(System.currentTimeMillis()));
        dao.updateStepExecution(stepExecution);
        assertNotEquals(versionAfterSave.intValue(), stepExecution.getVersion().intValue());

        StepExecution retrieved = dao.getStepExecution(jobExecution, stepExecution.getId());
        assertEquals(stepExecution, retrieved);
        assertEquals(stepExecution.getLastUpdated(), retrieved.getLastUpdated());
        assertEquals(BatchStatus.ABANDONED, retrieved.getStatus());
    }

    /**
     * Exception should be raised when the version of update argument doesn't
     * match the version of persisted entity.
     * <p>
     * Ignore this test, using Optimistic Locking capability of MarkLogic, update-policy=VERSION_OPTIONAL
     */
    @Transactional
    @Test
    public void testConcurrentModificationException() {
        step = new StepSupport("foo");

        StepExecution exec1 = new StepExecution(step.getName(), jobExecution);
        dao.saveStepExecution(exec1);

        StepExecution exec2 = new StepExecution(step.getName(), jobExecution);
        exec2.setId(exec1.getId());

        exec2.incrementVersion();
        exec2.setVersion(exec1.getVersion());
        //assertEquals(new Integer(0), exec1.getVersion());
        assertEquals(exec1.getVersion(), exec2.getVersion());

        dao.updateStepExecution(exec1);
        //assertEquals(new Integer(1), exec1.getVersion());

        try {
            dao.updateStepExecution(exec2);
            fail();
        } catch (OptimisticLockingFailureException e) {
            // expected
        }

    }

    @Test
    public void testGetStepExecutionsWhenNoneExist() throws Exception {
        int count = jobExecution.getStepExecutions().size();
        dao.addStepExecutions(jobExecution);
        assertEquals("Incorrect size of collection", count, jobExecution.getStepExecutions().size());
    }

    private void assertStepExecutionsAreEqual(StepExecution expected, StepExecution actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getEndTime(), actual.getEndTime());
        assertEquals(expected.getSkipCount(), actual.getSkipCount());
        assertEquals(expected.getCommitCount(), actual.getCommitCount());
        assertEquals(expected.getReadCount(), actual.getReadCount());
        assertEquals(expected.getWriteCount(), actual.getWriteCount());
        assertEquals(expected.getFilterCount(), actual.getFilterCount());
        assertEquals(expected.getWriteSkipCount(), actual.getWriteSkipCount());
        assertEquals(expected.getReadSkipCount(), actual.getReadSkipCount());
        assertEquals(expected.getProcessSkipCount(), actual.getProcessSkipCount());
        assertEquals(expected.getRollbackCount(), actual.getRollbackCount());
        assertEquals(expected.getExitStatus(), actual.getExitStatus());
        assertEquals(expected.getLastUpdated(), actual.getLastUpdated());
        assertEquals(expected.getExitStatus(), actual.getExitStatus());
        assertEquals(expected.getJobExecutionId(), actual.getJobExecutionId());
    }
}
