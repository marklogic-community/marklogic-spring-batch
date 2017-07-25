package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.*;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

/**
 * Base class for any "core" test.
 */
@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class})
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {

    protected ApplicationContext applicationContext;
    
    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao executionContextDao;
    private JobRepository jobRepository;
    private JobExplorer jobExplorer;
    
    protected void initializeJobRepository() {
        jobInstanceDao = new MarkLogicJobInstanceDao(getClient());
        jobExecutionDao = new MarkLogicJobExecutionDao(getClient());
        stepExecutionDao = new MarkLogicStepExecutionDao(getClient(), jobExecutionDao);
        executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);
        jobRepository = new MarkLogicSimpleJobRepository(jobInstanceDao, jobExecutionDao,
                            stepExecutionDao, executionContextDao);
        jobExplorer = new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
    }
    
    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }
    
    public JobInstanceDao getJobInstanceDao() {
        return jobInstanceDao;
    }
    
    public JobExecutionDao getJobExecutionDao() {
        return jobExecutionDao;
    }
    
    public StepExecutionDao getStepExecutionDao() {
        return stepExecutionDao;
    }
    
    public ExecutionContextDao getExecutionContextDao() {
        return executionContextDao;
    }
    
    public JobRepository getJobRepository() {
        return jobRepository;
    }
    
    public JobExplorer getJobExplorer() {
        return jobExplorer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean("batchDatabaseClientProvider", DatabaseClientProvider.class));
        setXccTemplate(applicationContext.getBean("xccTemplate", XccTemplate.class));
    }
    
    
}
