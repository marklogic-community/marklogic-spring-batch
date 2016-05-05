package com.marklogic.spring.batch.core.repository.dao;

import java.util.Collection;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.item.ExecutionContext;

public class MarkLogicExecutionContextDao extends AbstractMarkLogicBatchMetadataDao implements ExecutionContextDao {

	@Override
	public ExecutionContext getExecutionContext(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecutionContext getExecutionContext(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveExecutionContext(JobExecution jobExecution) {
		// TODO Auto-generated method stub

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
