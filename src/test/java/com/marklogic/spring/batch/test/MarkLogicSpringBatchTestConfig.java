package com.marklogic.spring.batch.test;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.client.helper.DatabaseClientProvider;

@Configuration
public class MarkLogicSpringBatchTestConfig {
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	@Bean
	public JobRepositoryTestUtils jobRepositoryTestUtils() {
		return new JobRepositoryTestUtils(databaseClientProvider.getDatabaseClient(), jobRepository, jobExplorer);
	}

}
