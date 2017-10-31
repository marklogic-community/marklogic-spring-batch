package com.marklogic.spring.batch.core.explore.support;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.AbstractJobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class MarkLogicJobExplorerFactoryBean extends AbstractJobExplorerFactoryBean implements InitializingBean {

    private DatabaseClient databaseClient;
    private BatchProperties batchProperties;
    private JobInstanceDao jobInstanceDao;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private ExecutionContextDao executionContextDao;

    /**
     * Public setter for the {@link DatabaseClient}.
     *
     * @param databaseClient a {@link DatabaseClient}
     */
    public void setDatabaseClient(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public void setJobRepositoryProperties(BatchProperties batchProperties) {
        this.batchProperties = batchProperties;
    }

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {
        return jobInstanceDao;
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        return jobExecutionDao;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        return stepExecutionDao;
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
        return executionContextDao;
    }

    @Override
    public JobExplorer getObject() throws Exception {
        return new SimpleJobExplorer(createJobInstanceDao(),
                createJobExecutionDao(), createStepExecutionDao(),
                createExecutionContextDao());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseClient, "DatabaseClient must not be null.");
        jobInstanceDao = new MarkLogicJobInstanceDao(databaseClient, batchProperties);
        jobExecutionDao = new MarkLogicJobExecutionDao(databaseClient, batchProperties);
        stepExecutionDao = new MarkLogicStepExecutionDao(databaseClient, jobExecutionDao, batchProperties);
        executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);
    }
}
