package com.marklogic.spring.batch.core.repository.support;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.config.JobRepositoryProperties;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.AbstractJobRepositoryFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class MarkLogicJobRepositoryFactoryBean extends AbstractJobRepositoryFactoryBean implements InitializingBean {

    private DatabaseClient databaseClient;
    private JobRepositoryProperties jobRepositoryProperties;
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

    public void setJobRepositoryProperties(JobRepositoryProperties jobRepositoryProperties) {
        this.jobRepositoryProperties = jobRepositoryProperties;
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
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseClient, "DatabaseClient must not be null.");
        jobInstanceDao = new MarkLogicJobInstanceDao(databaseClient, jobRepositoryProperties);
        jobExecutionDao = new MarkLogicJobExecutionDao(databaseClient);
        stepExecutionDao = new MarkLogicStepExecutionDao(databaseClient, jobExecutionDao);
        executionContextDao = new MarkLogicExecutionContextDao(jobExecutionDao, stepExecutionDao);
        super.afterPropertiesSet();
    }
}
