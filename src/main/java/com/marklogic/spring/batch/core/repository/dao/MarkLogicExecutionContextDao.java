package com.marklogic.spring.batch.core.repository.dao;

import java.util.Collection;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.Assert;

public class MarkLogicExecutionContextDao implements ExecutionContextDao {
	
	//private static final Log logger = LogFactory.getLog(MarkLogicExecutionContextDao.class);
	
	private JobExecutionDao jobExecutionDao;
	private StepExecutionDao stepExecutionDao;
	
	public MarkLogicExecutionContextDao(JobExecutionDao jobExecDao, StepExecutionDao stepExecDao) {
		this.jobExecutionDao = jobExecDao;
		this.stepExecutionDao = stepExecDao;
	}
	

	@Override
	public ExecutionContext getExecutionContext(JobExecution jobExecution) {
		return jobExecutionDao.getJobExecution(jobExecution.getId()).getExecutionContext();
	}

	@Override
	public ExecutionContext getExecutionContext(StepExecution stepExecution) {
		return stepExecutionDao.getStepExecution(stepExecution.getJobExecution(), stepExecution.getId()).getExecutionContext();
	}

	@Override
	public void saveExecutionContext(JobExecution jobExecution) {
		jobExecution.incrementVersion();
		jobExecutionDao.updateJobExecution(jobExecution);
	}

	@Override
	public void saveExecutionContext(StepExecution stepExecution) {
		stepExecution.incrementVersion();
		stepExecutionDao.updateStepExecution(stepExecution);

	}

	@Override
	public void saveExecutionContexts(Collection<StepExecution> stepExecutions) {
		Assert.notNull(stepExecutions, "Attempt to save an null collection of step executions");
		for (StepExecution stepExec : stepExecutions ) {
			saveExecutionContext(stepExec);
		}
	}

	@Override
	public void updateExecutionContext(JobExecution jobExecution) {
		jobExecutionDao.updateJobExecution(jobExecution);

	}

	@Override
	public void updateExecutionContext(StepExecution stepExecution) {
		stepExecutionDao.updateStepExecution(stepExecution);

	}

}
