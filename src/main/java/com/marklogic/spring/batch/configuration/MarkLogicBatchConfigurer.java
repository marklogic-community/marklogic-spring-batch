package com.marklogic.spring.batch.configuration;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class MarkLogicBatchConfigurer implements BatchConfigurer, InitializingBean {

    private static final Log logger = LogFactory.getLog(MarkLogicBatchConfigurer.class);

    private DatabaseClient databaseClient;
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    @Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return this.transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    protected MarkLogicBatchConfigurer() {}

    protected MarkLogicBatchConfigurer(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if(databaseClient == null) {
                logger.warn("No MarkLogic DatabaseClient was provided...using a Map based JobRepository");

                if(this.transactionManager == null) {
                    this.transactionManager = new ResourcelessTransactionManager();
                }

                MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.transactionManager);
                jobRepositoryFactory.afterPropertiesSet();
                this.jobRepository = jobRepositoryFactory.getObject();

                MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
                jobExplorerFactory.afterPropertiesSet();
                this.jobExplorer = jobExplorerFactory.getObject();
            } else {
                this.jobRepository = new MarkLogicSimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());;
                this.jobExplorer = new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());;
            }
            this.jobLauncher = createJobLauncher();
        } catch (Exception e) {
            throw new BatchConfigurationException(e);
        }
    }

    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    private JobExecutionDao jobExecutionDao() throws Exception {
        return new MarkLogicJobExecutionDao(databaseClient);
    }

    private JobInstanceDao jobInstanceDao() throws Exception {
        return new MarkLogicJobInstanceDao(databaseClient);
    }

    private StepExecutionDao stepExecutionDao() throws Exception {
        MarkLogicStepExecutionDao stepExecutionDao = new MarkLogicStepExecutionDao(databaseClient);
        stepExecutionDao.setJobExecutionDao(jobExecutionDao());
        return stepExecutionDao;
    }

    private ExecutionContextDao executionContextDao() throws Exception {
        return new MarkLogicExecutionContextDao(jobExecutionDao(), stepExecutionDao());
    }


}
