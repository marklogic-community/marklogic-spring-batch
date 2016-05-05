package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;

@Configuration
@Import( com.marklogic.client.spring.BasicConfig.class )
public class MarkLogicDaoConfig {
	
	@Autowired
	public DatabaseClientProvider databaseClientProvider;
	
	@Bean
	public JobExecutionDao jobExecutionDao() throws Exception {
		MarkLogicJobExecutionDao dao = new MarkLogicJobExecutionDao(databaseClientProvider.getDatabaseClient());
		dao.setIncrementer(new UriIncrementer());
		return dao;
	}
	
	@Bean
	public JobInstanceDao jobInstanceDao() throws Exception {
		MarkLogicJobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao(databaseClientProvider.getDatabaseClient());
		jobInstanceDao.setIncrementer(new UriIncrementer());
		jobInstanceDao.setJobExecutionDao(jobExecutionDao());
		return jobInstanceDao;
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
		MarkLogicSimpleJobRepository jobRepository = new MarkLogicSimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
		return jobRepository;
	}
	
}
