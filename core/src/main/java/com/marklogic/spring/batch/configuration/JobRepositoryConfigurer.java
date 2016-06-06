package com.marklogic.spring.batch.configuration;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * BatchConfigurer that is used to enable the MarkLogic JobRepository implementation.
 */
public class JobRepositoryConfigurer extends DefaultBatchConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(JobRepositoryConfigurer.class);

    public final static String JOB_REPOSITORY_DATABASE_CLIENT_PROVIDER_BEAN_NAME = "jobRepositoryDatabaseClientProvider";

    @Autowired(required = false)
    @Qualifier(JOB_REPOSITORY_DATABASE_CLIENT_PROVIDER_BEAN_NAME)
    private DatabaseClientProvider databaseClientProvider;

    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    /**
     * If our specific DatabaseClientProvider bean is found, then enable the MarkLogic JobRepository
     * implementation. Otherwise, defer to the parent class method, which will fall back to an in-memory
     * implementation.
     */
    @Override
    public void initialize() {
        if (databaseClientProvider != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Initializing MarkLogic JobRepository implementation");
            }
            DatabaseClient client = databaseClientProvider.getDatabaseClient();

            JobInstanceDao jobInstanceDao = new MarkLogicJobInstanceDao(client);
            JobExecutionDao jobExecutionDao = new MarkLogicJobExecutionDao(client);
            MarkLogicStepExecutionDao stepExecutionDao = new MarkLogicStepExecutionDao(client);
            stepExecutionDao.setJobExecutionDao(jobExecutionDao);
            ExecutionContextDao executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);

            this.jobRepository = new MarkLogicSimpleJobRepository(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
            this.jobExplorer = new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
            this.transactionManager = new ResourcelessTransactionManager();

            SimpleJobLauncher jbl = new SimpleJobLauncher();
            jbl.setJobRepository(jobRepository);
            try {
                jbl.afterPropertiesSet();
            } catch (Exception e) {
                throw new BatchConfigurationException(e);
            }
            this.jobLauncher = jbl;
        } else {
            super.initialize();
        }
    }

    @Override
    public JobRepository getJobRepository() {
        return jobRepository != null ? jobRepository : super.getJobRepository();
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager != null ? transactionManager : super.getTransactionManager();
    }

    @Override
    public JobLauncher getJobLauncher() {
        return jobLauncher != null ? jobLauncher : super.getJobLauncher();
    }

    @Override
    public JobExplorer getJobExplorer() {
        return jobExplorer != null ? jobExplorer : super.getJobExplorer();
    }
}
