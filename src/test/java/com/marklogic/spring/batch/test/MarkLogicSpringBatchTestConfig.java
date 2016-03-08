package com.marklogic.spring.batch.test;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarkLogicSpringBatchTestConfig {
	
	@Autowired
	private JobRepository jobRepository;
	
	@Bean
	public JobRepositoryTestUtils jobRepositoryTestUtils() {
		return new JobRepositoryTestUtils(jobRepository);
	}

}
