package com.marklogic.spring.batch.core.repository.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.item.ExecutionContext;

public class MarkLogicExecutionContextDao implements ExecutionContextDao {
	
	private static final Log logger = LogFactory.getLog(MarkLogicExecutionContextDao.class);
	
	private JobExecutionDao jobExecutionDao;
	
	
	public MarkLogicExecutionContextDao(JobExecutionDao jobExecDao) {
		this.jobExecutionDao = jobExecDao;
	}
	

	@Override
	public ExecutionContext getExecutionContext(JobExecution jobExecution) {
		return jobExecutionDao.getJobExecution(jobExecution.getId()).getExecutionContext();
	}

	@Override
	public ExecutionContext getExecutionContext(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveExecutionContext(JobExecution jobExecution) {
		jobExecution.incrementVersion();
		jobExecutionDao.saveJobExecution(jobExecution);
	}

	@Override
	public void saveExecutionContext(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveExecutionContexts(Collection<StepExecution> stepExecutions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecutionContext(JobExecution jobExecution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecutionContext(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

}
