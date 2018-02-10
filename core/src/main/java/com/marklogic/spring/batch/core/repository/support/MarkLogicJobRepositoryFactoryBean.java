package com.marklogic.spring.batch.core.repository.support;

import com.marklogic.client.DatabaseClient;
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
    private MarkLogicJobRepositoryProperties properties;

    public void setDatabaseClient(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public void setMarkLogicJobRepositoryProperties(MarkLogicJobRepositoryProperties properties) {
        this.properties = properties;
    }

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {
        return new MarkLogicJobInstanceDao(databaseClient, properties);
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        return new MarkLogicJobExecutionDao(databaseClient, properties);
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        return new MarkLogicStepExecutionDao(databaseClient, properties);
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
        return new MarkLogicExecutionContextDao(databaseClient, properties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(databaseClient, "DatabaseClient cannot be NULL");
        super.afterPropertiesSet();
    }
}
