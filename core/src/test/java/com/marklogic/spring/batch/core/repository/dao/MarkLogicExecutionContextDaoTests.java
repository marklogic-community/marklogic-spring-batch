package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MarkLogicExecutionContextDaoTests extends AbstractJobRepositoryTest {

    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao dao;
    private JobExecution jobExecution;
    private StepExecution stepExecution;

    @Before
    public void setUp() {
        jobInstanceDao = new MarkLogicJobInstanceDao(getClient(), getBatchProperties());
        jobExecutionDao = new MarkLogicJobExecutionDao(getClient(), getBatchProperties());
        stepExecutionDao = new MarkLogicStepExecutionDao(getClient(), getBatchProperties());
        dao = new MarkLogicExecutionContextDao(getClient(), getBatchProperties());

        JobInstance ji = jobInstanceDao.createJobInstance("testJob", new JobParameters());
        jobExecution = new JobExecution(ji, new JobParameters());
        jobExecutionDao.saveJobExecution(jobExecution);
        stepExecution = new StepExecution("stepName", jobExecution);
        stepExecutionDao.saveStepExecution(stepExecution);

    }

    @Transactional
    @Test
    public void testSaveAndFindJobContext() {
        ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(jobExecution);

        ExecutionContext retrieved = dao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindExecutionContexts() {

        List<StepExecution> stepExecutions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JobInstance ji = jobInstanceDao.createJobInstance("testJob" + i, new JobParameters());
            JobExecution je = new JobExecution(ji, new JobParameters());
            jobExecutionDao.saveJobExecution(je);
            StepExecution se = new StepExecution("step" + i, je);
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
        stepExecutionDao.saveStepExecutions(stepExecutions);
        dao.saveExecutionContexts(stepExecutions);

        for (int i = 0; i < 3; i++) {
            ExecutionContext retrieved = dao.getExecutionContext(stepExecutions.get(i));
            assertEquals(stepExecutions.get(i).getExecutionContext(), retrieved);
        }
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testSaveNullExecutionContexts() {
        dao.saveExecutionContexts(null);
    }

    @Transactional
    @Test
    public void testSaveEmptyExecutionContexts() {
        dao.saveExecutionContexts(new ArrayList<StepExecution>());
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyJobContext() {

        ExecutionContext ctx = new ExecutionContext();
        jobExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(jobExecution);

        ExecutionContext retrieved = dao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateContext() {

        ExecutionContext ctx = new ExecutionContext(Collections
                .singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(jobExecution);

        ctx.putLong("longKey", 7);
        dao.updateExecutionContext(jobExecution);

        ExecutionContext retrieved = dao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
        assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testSaveAndFindStepContext() {

        ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(stepExecution);

        ExecutionContext retrieved = dao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyStepContext() {

        ExecutionContext ctx = new ExecutionContext();
        stepExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(stepExecution);

        ExecutionContext retrieved = dao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateStepContext() {

        ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        dao.saveExecutionContext(stepExecution);

        ctx.putLong("longKey", 7);
        dao.updateExecutionContext(stepExecution);

        ExecutionContext retrieved = dao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
        assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testStoreInteger() {
        ExecutionContext ec = new ExecutionContext();
        ec.put("intValue", 343232);
        stepExecution.setExecutionContext(ec);
        dao.saveExecutionContext(stepExecution);
        ExecutionContext restoredEc = dao.getExecutionContext(stepExecution);
        assertEquals(ec, restoredEc);
    }


}
