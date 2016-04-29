package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import com.marklogic.client.helper.DatabaseClientProvider;

@Configuration
@Import( com.marklogic.junit.spring.BasicTestConfig.class )
public class MarkLogicDaoConfig extends AbstractBatchConfiguration {
	
	@Autowired
	public DatabaseClientProvider databaseClientProvider;
	
	@Autowired
	public ApplicationContext ctx;
	
	@Bean
	public JobExecutionDao jobExecutionDao() throws Exception {
		MarkLogicJobExecutionDao jobExecutionDao = new MarkLogicJobExecutionDao();
		jobExecutionDao.setDatabaseClient(databaseClientProvider.getDatabaseClient());
		return jobExecutionDao;
	}
	
	@Bean
	public JobInstanceDao jobInstanceDao() {
		MarkLogicJobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao();
		jobInstanceDao.setDatabaseClient(databaseClientProvider.getDatabaseClient());
		return jobInstanceDao;
	}

	@Override
	public JobRepository jobRepository() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobLauncher jobLauncher() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExplorer jobExplorer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlatformTransactionManager transactionManager() throws Exception {
		// TODO Auto-generated method stub
		return null;
	} 
	
}
