package com.marklogic.spring.batch.configuration;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.MapExecutionContextDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;

@Configuration
@Profile("marklogic")
public class MarkLogicBatchConfiguration extends AbstractBatchConfiguration {
	
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
		return new MapExecutionContextDao();
	}
	
	@Bean
	public JobRepository jobRepository() throws Exception {
		return new MarkLogicSimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
	}
	
	@Bean
	protected TaskExecutor taskExecutor() {
		//CustomizableThreadFactory tf = new CustomizableThreadFactory("geoname-threads");
		//SimpleAsyncTaskExecutor sate =  new SimpleAsyncTaskExecutor(tf);
		//sate.setConcurrencyLimit(8);
		return new SyncTaskExecutor();
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
	public JobBuilderFactory jobBuilders() throws Exception {
		return new JobBuilderFactory(jobRepository());
	}
	
	@Bean
	public StepBuilderFactory stepBuilders() throws Exception {
		return new StepBuilderFactory(jobRepository(), transactionManager());
	}
	
	@Bean
	public JobExplorer jobExplorer() throws Exception {
		return new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository());
		launcher.setTaskExecutor(taskExecutor());
		return launcher;
	}
	
	@Bean
	public JobParametersBuilder jobParametersBuilder() {
		return new JobParametersBuilder();
	}
}
