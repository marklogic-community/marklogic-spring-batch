package com.marklogic.spring.batch.core.repository.dao;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;

public class AbstractMarkLogicBatchMetadataDao implements org.springframework.beans.factory.InitializingBean {
	
	protected DatabaseClient databaseClient;
	
	protected final long LOWER_RANGE = 0; 
    protected final long UPPER_RANGE = 9999999;
    
    public final String SEARCH_OPTIONS_NAME = "spring-batch";
    
	public final String JOB_EXECUTION_NAMESPACE = "http://marklogic.com/spring-batch";
	public final String JOB_EXECUTION_NAMESPACE_PREFIX = "msb";
	
	public final String JOB_INSTANCE_NAMESPACE = "http://marklogic.com/spring-batch/job-instance";
	public final String JOB_INSTANCE_NAMESPACE_PREFIX = "inst";
	
	public final String STEP_EXECUTION_NAMESPACE = "http://marklogic.com/spring-batch/step-execution";
	public final String STEP_EXECUTION_NAMESPACE_PREFIX = "step";
	
	public final String JOB_PARAMETER_NAMESPACE = "http://marklogic.com/spring-batch/job-parameter";
	public final String JOB_PARAMETER_NAMESPACE_PREFIX = "jp";
	
	public final String SPRING_BATCH_DIR = "/projects.spring.io/spring-batch/";
	public final String COLLECTION_JOB_EXECUTION = "http://marklogic.com/spring-batch/job-execution";

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public void setDatabaseClient(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(databaseClient);
	}
	
	public Long generateId() {
		return ThreadLocalRandom.current().nextLong();
	}

}
