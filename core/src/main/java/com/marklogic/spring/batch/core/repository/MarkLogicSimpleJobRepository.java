package com.marklogic.spring.batch.core.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.util.Assert;

public class MarkLogicSimpleJobRepository extends SimpleJobRepository {
	
	//private static final Log logger = LogFactory.getLog(SimpleJobRepository.class);

	//private JobInstanceDao jobInstanceDao;

	//private JobExecutionDao jobExecutionDao;

	private StepExecutionDao stepExecutionDao;

	private ExecutionContextDao ecDao;

	public MarkLogicSimpleJobRepository(JobInstanceDao jobInstanceDao, JobExecutionDao jobExecutionDao,
			StepExecutionDao stepExecutionDao, ExecutionContextDao ecDao) {
		super(jobInstanceDao, jobExecutionDao, stepExecutionDao, ecDao);
		this.stepExecutionDao = stepExecutionDao;
		this.ecDao = ecDao;
	}

	
	@Override
	public void add(StepExecution stepExecution) {
		validateStepExecution(stepExecution);
		JobExecution jobExec = stepExecution.getJobExecution();
		
		//SimpleJobRepository is missing this line
		List<StepExecution> steps = new ArrayList<>(jobExec.getStepExecutions());
		steps.add(stepExecution);
		jobExec.addStepExecutions(steps);
		
		stepExecution.setLastUpdated(new Date(System.currentTimeMillis()));
		stepExecutionDao.saveStepExecution(stepExecution);
		ecDao.saveExecutionContext(stepExecution);
	}
	
	private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution, "StepExecution cannot be null.");
		Assert.notNull(stepExecution.getStepName(), "StepExecution's step name cannot be null.");
		Assert.notNull(stepExecution.getJobExecutionId(), "StepExecution must belong to persisted JobExecution");
	}
}
