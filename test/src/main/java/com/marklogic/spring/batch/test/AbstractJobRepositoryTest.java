package com.marklogic.spring.batch.test;

import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicExecutionContextDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobExecutionDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicJobInstanceDao;
import com.marklogic.spring.batch.core.repository.dao.MarkLogicStepExecutionDao;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;

public abstract class AbstractJobRepositoryTest extends AbstractSpringBatchTest {

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



}
