package com.marklogic.spring.batch.configuration;

import com.marklogic.spring.batch.bind.*;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;

import java.util.Collection;

@Configuration
@PropertySource("classpath:job.properties")
@ComponentScan( { "com.marklogic.spring.batch.configuration" } )
public class MarkLogicBatchConfiguration extends AbstractBatchConfiguration {
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;

    private BatchConfigurer configurer;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MarkLogicSimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
    }

    @Override
    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository());
        launcher.setTaskExecutor(taskExecutor());
        return launcher;
    }

    @Override
    @Bean
    public JobExplorer jobExplorer() throws Exception {
        return new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
    }

    @Override
    @Bean
    public PlatformTransactionManager transactionManager() {

        return new ResourcelessTransactionManager();
    }

	private TaskExecutor taskExecutor() {
		//CustomizableThreadFactory tf = new CustomizableThreadFactory("geoname-threads");
		//SimpleAsyncTaskExecutor sate =  new SimpleAsyncTaskExecutor(tf);
		//sate.setConcurrencyLimit(8);
		return new SyncTaskExecutor();
	}

    @Override
    protected BatchConfigurer getConfigurer(Collection<BatchConfigurer> configurers) throws Exception {
        if (this.configurer != null) {
            return this.configurer;
        }
        MarkLogicBatchConfigurer configurer = new MarkLogicBatchConfigurer(databaseClientProvider.getDatabaseClient());
        this.configurer = configurer;
        return this.configurer;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                com.marklogic.spring.batch.core.AdaptedJobExecution.class,
                com.marklogic.spring.batch.core.AdaptedJobInstance.class,
                com.marklogic.spring.batch.core.AdaptedJobParameters.class,
                com.marklogic.spring.batch.core.AdaptedStepExecution.class,
                com.marklogic.spring.batch.core.AdaptedExecutionContext.class,
                com.marklogic.spring.batch.core.MarkLogicJobInstance.class);
        marshaller.setAdapters(
                new ExecutionContextAdapter(),
                new JobExecutionAdapter(),
                new JobInstanceAdapter(),
                new JobParametersAdapter(),
                new StepExecutionAdapter());
        //marshaller.setMarshallerProperties(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    private JobExecutionDao jobExecutionDao() throws Exception {
        return new MarkLogicJobExecutionDao(databaseClientProvider.getDatabaseClient());
    }

    private JobInstanceDao jobInstanceDao() throws Exception {
        return new MarkLogicJobInstanceDao(databaseClientProvider.getDatabaseClient());
    }

    private StepExecutionDao stepExecutionDao() throws Exception {
        MarkLogicStepExecutionDao stepExecutionDao = new MarkLogicStepExecutionDao(databaseClientProvider.getDatabaseClient());
        stepExecutionDao.setJobExecutionDao(jobExecutionDao());
        return stepExecutionDao;
    }

    private ExecutionContextDao executionContextDao() throws Exception {
        return new MarkLogicExecutionContextDao(jobExecutionDao(), stepExecutionDao());
    }

}
