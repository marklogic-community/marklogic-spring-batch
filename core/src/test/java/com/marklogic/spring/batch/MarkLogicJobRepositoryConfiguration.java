package com.marklogic.spring.batch;


import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.bind.*;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Tests that need to exercise the MarkLogic implementation of a JobRepository should include this
 * in their Spring configuration.
 */
@Configuration
public class MarkLogicJobRepositoryConfiguration {

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MarkLogicSimpleJobRepository(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
    }

    @Bean
    public JobExplorer jobExplorer() throws Exception {
        return new SimpleJobExplorer(jobInstanceDao(), jobExecutionDao(), stepExecutionDao(), executionContextDao());
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
