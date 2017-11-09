package com.marklogic.spring.batch.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;

public class YourJobListener implements JobExecutionListener, StepExecutionListener, ChunkListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    public void afterChunk(ChunkContext context) {
        logger.info("CHUNK COMPLETE");
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("BeforeJob: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("AfterJob: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("BeforeStep: " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("AfterStep: " + stepExecution.getStepName());
        return ExitStatus.COMPLETED;
    }
}
