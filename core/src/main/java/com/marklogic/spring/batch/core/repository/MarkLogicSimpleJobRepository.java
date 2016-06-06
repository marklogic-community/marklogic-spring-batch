package com.marklogic.spring.batch.core.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.restapi.RestApi;

import com.marklogic.mgmt.restapis.RestApiManager;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class MarkLogicSimpleJobRepository extends SimpleJobRepository {

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

	public static void deploy(String host, int port, String username, String password) {
		MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig();
		ManageConfig manageConfig = new ManageConfig(host, 8002, username, password);
		ManageClient manageClient = new ManageClient(manageConfig);

        RestApiManager restApiMgr = new RestApiManager(manageClient);
        restApiMgr.createRestApi(config.getRestApiConfig().toString());
    }

	private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution, "StepExecution cannot be null.");
		Assert.notNull(stepExecution.getStepName(), "StepExecution's step name cannot be null.");
		Assert.notNull(stepExecution.getJobExecutionId(), "StepExecution must belong to persisted JobExecution");
	}
}
