package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.junit.spring.AbstractSpringTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { com.marklogic.spring.batch.core.repository.dao.MarkLogicDaoConfig.class, com.marklogic.junit.spring.BasicTestConfig.class })
public class MarkLogicExecutionContextDaoTests extends AbstractSpringTest {
	
	@Autowired
	private JobInstanceDao jobInstanceDao;

	@Autowired
	private JobExecutionDao jobExecutionDao;

	@Autowired
	private StepExecutionDao stepExecutionDao;

	@Autowired
	private ExecutionContextDao contextDao;

	private JobExecution jobExecution;

	private StepExecution stepExecution;

	@Before
	public void setUp() {
		jobInstanceDao = getJobInstanceDao();
		jobExecutionDao = getJobExecutionDao();
		stepExecutionDao = getStepExecutionDao();
		contextDao = getExecutionContextDao();

		JobInstance ji = jobInstanceDao.createJobInstance("testJob", new JobParameters());
		jobExecution = new JobExecution(ji, new JobParameters());
		jobExecutionDao.saveJobExecution(jobExecution);
		stepExecution = new StepExecution("stepName", jobExecution);
		stepExecutionDao.saveStepExecution(stepExecution);

	}

	/**
	 * @return Configured {@link ExecutionContextDao} implementation ready for
	 * use.
	 */
	protected JobExecutionDao getJobExecutionDao() {
		return this.jobExecutionDao;
	}

	/**
	 * @return Configured {@link ExecutionContextDao} implementation ready for
	 * use.
	 */
	protected JobInstanceDao getJobInstanceDao() {
		return this.jobInstanceDao;
	}

	/**
	 * @return Configured {@link ExecutionContextDao} implementation ready for
	 * use.
	 */
	protected StepExecutionDao getStepExecutionDao() {
		return this.stepExecutionDao;
	}

	/**
	 * @return Configured {@link ExecutionContextDao} implementation ready for
	 * use.
	 */
	protected ExecutionContextDao getExecutionContextDao() {
		return this.contextDao;
	}

	@Transactional
	@Test
	public void testSaveAndFindJobContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.<String, Object> singletonMap("key", "value"));
		jobExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(jobExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testSaveAndFindExecutionContexts() {

		List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
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
		contextDao.saveExecutionContexts(stepExecutions);

		for (int i = 0; i < 3; i++) {
			ExecutionContext retrieved = contextDao.getExecutionContext(stepExecutions.get(i).getJobExecution());
			assertEquals(stepExecutions.get(i).getExecutionContext(), retrieved);
		}
	}

	@Transactional
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullExecutionContexts() {
		contextDao.saveExecutionContexts(null);
	}

	@Transactional
	@Test
	public void testSaveEmptyExecutionContexts() {
		contextDao.saveExecutionContexts(new ArrayList<StepExecution>());
	}

	@Transactional
	@Test
	public void testSaveAndFindEmptyJobContext() {

		ExecutionContext ctx = new ExecutionContext();
		jobExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(jobExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testUpdateContext() {

		ExecutionContext ctx = new ExecutionContext(Collections
				.<String, Object> singletonMap("key", "value"));
		jobExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(jobExecution);

		ctx.putLong("longKey", 7);
		contextDao.updateExecutionContext(jobExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
		assertEquals(ctx, retrieved);
		assertEquals(7, retrieved.getLong("longKey"));
	}

	@Transactional
	@Test
	public void testSaveAndFindStepContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.<String, Object> singletonMap("key", "value"));
		stepExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(stepExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testSaveAndFindEmptyStepContext() {

		ExecutionContext ctx = new ExecutionContext();
		stepExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(stepExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
	}

	@Transactional
	@Test
	public void testUpdateStepContext() {

		ExecutionContext ctx = new ExecutionContext(Collections.<String, Object> singletonMap("key", "value"));
		stepExecution.setExecutionContext(ctx);
		contextDao.saveExecutionContext(stepExecution);

		ctx.putLong("longKey", 7);
		contextDao.updateExecutionContext(stepExecution);

		ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
		assertEquals(ctx, retrieved);
		assertEquals(7, retrieved.getLong("longKey"));
	}

	@Transactional
	@Test
	public void testStoreInteger() {

		ExecutionContext ec = new ExecutionContext();
		ec.put("intValue", new Integer(343232));
		stepExecution.setExecutionContext(ec);
		contextDao.saveExecutionContext(stepExecution);
		ExecutionContext restoredEc = contextDao.getExecutionContext(stepExecution);
		assertEquals(ec, restoredEc);
	}


}
