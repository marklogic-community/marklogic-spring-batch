package com.marklogic.spring.batch.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;

public class MarkLogicBatchConfigurer implements BatchConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(MarkLogicBatchConfigurer.class);
    private DatabaseClient databaseClient;
    private BatchProperties batchProperties;

    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao executionContextDao;

    private JobRepository jobRepository;
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;
    private PlatformTransactionManager transactionManager;

    protected MarkLogicBatchConfigurer() {
    }

    public MarkLogicBatchConfigurer(DatabaseClientProvider databaseClientProvider, BatchProperties batchProperties) {
        this.databaseClient = databaseClientProvider.getDatabaseClient();
        this.batchProperties = batchProperties;
    }

    @PostConstruct
    public void initialize() throws Exception {
        if (databaseClient == null) {
            logger.warn("No DatabaseClient was provided...using a Map based JobRepository");
            if (this.transactionManager == null) {
                this.transactionManager = new ResourcelessTransactionManager();
            }
        } else {
            jobInstanceDao = new MarkLogicJobInstanceDao(databaseClient, batchProperties);
            jobExecutionDao = new MarkLogicJobExecutionDao(databaseClient, batchProperties);
            stepExecutionDao = new MarkLogicStepExecutionDao(databaseClient, batchProperties);
            executionContextDao = new MarkLogicExecutionContextDao(databaseClient, batchProperties);

            this.transactionManager = new ResourcelessTransactionManager();
            this.jobRepository = createJobRepository();
            this.jobExplorer = createJobExplorer();
        }
        this.jobLauncher = createJobLauncher();
    }

    protected JobRepository createJobRepository() throws Exception {
        jobRepository = new SimpleJobRepository(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
        return jobRepository;
    }

    protected JobExplorer createJobExplorer() throws Exception {
        jobExplorer = new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
        return jobExplorer;
    }

    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        return jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return jobExplorer;
    }

}
