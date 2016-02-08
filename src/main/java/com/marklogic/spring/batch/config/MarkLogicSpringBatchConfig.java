package com.marklogic.spring.batch.config;

import javax.xml.bind.JAXBContext;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.explore.MarkLogicJobExplorer;
import com.marklogic.spring.batch.core.repository.MarkLogicJobRepository;

@ActiveProfiles("marklogic")
@Configuration
public class MarkLogicSpringBatchConfig {
	
	private JAXBContext jaxbContext;
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;

	@Bean
	public JAXBContext jaxbContext() throws Exception {
		jaxbContext = JAXBContext.newInstance(org.springframework.batch.core.JobExecution.class,
                    org.springframework.batch.core.JobInstance.class);
		return jaxbContext;
	}	
	
	@Bean
	public JobRepository jobRepository() {
		return new MarkLogicJobRepository(databaseClientProvider.getDatabaseClient());
	}
	
	@Bean
	public JobLauncher jobLauncher() {
		return new SimpleJobLauncher();
	}

	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}
	
	@Bean
	public JobBuilderFactory jobBuilders() {
		return new JobBuilderFactory(jobRepository());
	}
	
	@Bean
	public StepBuilderFactory stepBuilders() {
		return new StepBuilderFactory(jobRepository(), transactionManager());
	}
	
	@Bean
	public JobExplorer jobExplorer() {
		return new MarkLogicJobExplorer(databaseClientProvider.getDatabaseClient());
	}
}
