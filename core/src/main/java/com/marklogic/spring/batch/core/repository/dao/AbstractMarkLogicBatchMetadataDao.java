package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.marklogic.client.DatabaseClient;

public abstract class AbstractMarkLogicBatchMetadataDao {

    protected DatabaseClient databaseClient;

    protected DataFieldMaxValueIncrementer incrementer;

    protected final long LOWER_RANGE = 0;
    protected final long UPPER_RANGE = 9999999;

    public final String SEARCH_OPTIONS_NAME = "spring-batch";

    public final String SPRING_BATCH_DIR = "/projects.spring.io/spring-batch/";

    public final String COLLECTION_JOB_INSTANCE = "http://marklogic.com/spring-batch/job-instance";

    public DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

    public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
        this.incrementer = incrementer;
    }

}
