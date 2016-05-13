package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;

@Configuration
@Import( com.marklogic.client.spring.BasicConfig.class )
public class MarkLogicDaoConfig {
	
	@Autowired
	public DatabaseClientProvider databaseClientProvider;
	
	@Bean
	public JobInstanceDao jobInstanceDao() throws Exception {
		MarkLogicJobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao(databaseClientProvider.getDatabaseClient());
		jobInstanceDao.setIncrementer(new UriIncrementer());
		return jobInstanceDao;
	}
	
	@Bean
	public JobExecutionDao jobExecutionDao() throws Exception {
		MarkLogicJobExecutionDao dao = new MarkLogicJobExecutionDao(databaseClientProvider.getDatabaseClient(), jobInstanceDao());
		dao.setIncrementer(new UriIncrementer());
		return dao;
	}
	
	@Bean
	public StepExecutionDao stepExecutionDao() throws Exception {
		MarkLogicStepExecutionDao stepExecutionDao = new MarkLogicStepExecutionDao(databaseClientProvider.getDatabaseClient());
		stepExecutionDao.setJobExecutionDao(jobExecutionDao());
		stepExecutionDao.setIncrementer(new UriIncrementer());
		return stepExecutionDao;
	}
	
	@Bean
	public ExecutionContextDao executionContextDao() throws Exception {
		MarkLogicExecutionContextDao executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao(), stepExecutionDao());
		return executionContextDao;
	}
	
	@Bean
	public JobRepository jobRepository() throws Exception {
		SimpleJobRepository jobRepository = new SimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
		return jobRepository;
	}

	@Bean
	public JobExplorer jobExplorer() throws Exception {
		return new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
	}
	
}
