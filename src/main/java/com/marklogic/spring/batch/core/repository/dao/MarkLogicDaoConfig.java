package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
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
	public JobExecutionDao jobExecutionDao() throws Exception {
		MarkLogicJobExecutionDao dao = new MarkLogicJobExecutionDao();
		dao.setDatabaseClient(databaseClientProvider.getDatabaseClient());
		dao.setJobExecutionIncrementer(new UriIncrementer());
		return new MarkLogicJobExecutionDao();
	}
	
	@Bean
	public JobInstanceDao jobInstanceDao() {
		MarkLogicJobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao();
		jobInstanceDao.setDatabaseClient(databaseClientProvider.getDatabaseClient());
		return jobInstanceDao;
	}	
	
}
