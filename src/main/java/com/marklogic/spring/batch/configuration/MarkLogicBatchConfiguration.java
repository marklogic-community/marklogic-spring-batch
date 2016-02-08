package com.marklogic.spring.batch.configuration;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
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
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.explore.MarkLogicJobExplorer;
import com.marklogic.spring.batch.core.repository.MarkLogicJobRepository;

@Configuration
@Profile("marklogic")
public class MarkLogicBatchConfiguration extends AbstractBatchConfiguration {
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;
	
	@Bean
	protected TaskExecutor taskExecutor() {
		//CustomizableThreadFactory tf = new CustomizableThreadFactory("geoname-threads");
		//SimpleAsyncTaskExecutor sate =  new SimpleAsyncTaskExecutor(tf);
		//sate.setConcurrencyLimit(8);
		SyncTaskExecutor ste = new SyncTaskExecutor();
		return ste;
	}
	
	@Bean
	public JobRepository jobRepository() {
		JobRepository jobRepo = new MarkLogicJobRepository(databaseClientProvider.getDatabaseClient());
	    return jobRepo;
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

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository());
		launcher.setTaskExecutor(taskExecutor());
		return launcher;
	}
}
