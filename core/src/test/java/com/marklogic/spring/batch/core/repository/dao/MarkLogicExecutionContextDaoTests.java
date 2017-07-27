package com.marklogic.spring.batch.core.repository.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class MarkLogicExecutionContextDaoTests extends AbstractJobRepositoryTest {

	private JobExecution jobExecution;

	private StepExecution stepExecution;

	@Before
	public void setUp() {
		initializeJobRepository();
		JobInstance ji = getJobInstanceDao().createJobInstance("testJob", new JobParameters());
		jobExecution = new JobExecution(ji, new JobParameters());
		getJobExecutionDao().saveJobExecution(jobExecution);
		stepExecution = new StepExecution("stepName", jobExecution);
		getStepExecutionDao().saveStepExecution(stepExecution);

	}

	@Transactional
	@Test
	public void testSaveAndFindJobContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
		jobExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(jobExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testSaveAndFindExecutionContexts() {

		List<StepExecution> stepExecutions = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			JobInstance ji = getJobInstanceDao().createJobInstance("testJob" + i, new JobParameters());
			JobExecution je = new JobExecution(ji, new JobParameters());
			getJobExecutionDao().saveJobExecution(je);
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
		getStepExecutionDao().saveStepExecutions(stepExecutions);
		getExecutionContextDao().saveExecutionContexts(stepExecutions);

		for (int i = 0; i < 3; i++) {
			ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(stepExecutions.get(i).getJobExecution());
			assertEquals(stepExecutions.get(i).getExecutionContext(), retrieved);
		}
	}

	@Transactional
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullExecutionContexts() {
		getExecutionContextDao().saveExecutionContexts(null);
	}

	@Transactional
	@Test
	public void testSaveEmptyExecutionContexts() {
		getExecutionContextDao().saveExecutionContexts(new ArrayList<StepExecution>());
	}

	@Transactional
	@Test
	public void testSaveAndFindEmptyJobContext() {

		ExecutionContext ctx = new ExecutionContext();
		jobExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(jobExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testUpdateContext() {

		ExecutionContext ctx = new ExecutionContext(Collections
				.singletonMap("key", "value"));
		jobExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(jobExecution);

		ctx.putLong("longKey", 7);
		getExecutionContextDao().updateExecutionContext(jobExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
		assertEquals(7, retrieved.getLong("longKey"));
	}

	@Transactional
	@Test
	public void testSaveAndFindStepContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
		stepExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(stepExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testSaveAndFindEmptyStepContext() {

		ExecutionContext ctx = new ExecutionContext();
		stepExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(stepExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testUpdateStepContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.singletonMap("key", "value"));
		stepExecution.setExecutionContext(ctx);
		getExecutionContextDao().saveExecutionContext(stepExecution);

		ctx.putLong("longKey", 7);
		getExecutionContextDao().updateExecutionContext(stepExecution);

		ExecutionContext retrieved = getExecutionContextDao().getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
		assertEquals(7, retrieved.getLong("longKey"));
	}

	@Transactional
	@Test
	public void testStoreInteger() {

		ExecutionContext ec = new ExecutionContext();
		ec.put("intValue", 343232);
		stepExecution.setExecutionContext(ec);
		getExecutionContextDao().saveExecutionContext(stepExecution);
		ExecutionContext restoredEc = getExecutionContextDao().getExecutionContext(stepExecution);
		assertEquals(ec, restoredEc);
	}


}
