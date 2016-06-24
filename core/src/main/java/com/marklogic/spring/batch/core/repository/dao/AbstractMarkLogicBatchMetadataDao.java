package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.marklogic.client.DatabaseClient;

public abstract class AbstractMarkLogicBatchMetadataDao extends LoggingObject {

	@Autowired
	protected ApplicationContext ctx;
	
	protected DatabaseClient databaseClient;
	
	protected DataFieldMaxValueIncrementer incrementer;

	protected final long LOWER_RANGE = 0;
    protected final long UPPER_RANGE = 9999999;
    
    public final String SEARCH_OPTIONS_NAME = "spring-batch";
	
	public final String SPRING_BATCH_DIR = "/projects.spring.io/spring-batch/";
	public final String SPRING_BATCH_INSTANCE_DIR = SPRING_BATCH_DIR + "instance/";
	
	public final String COLLECTION_JOB_EXECUTION = "http://marklogic.com/spring-batch/job-execution";
	public final String COLLECTION_JOB_INSTANCE = "http://marklogic.com/spring-batch/job-instance";
	public final String COLLECTION_STEP_EXECUTION = "http://marklogic.com/spring-batch/step-execution";

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public void setDatabaseClient(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}
	
	public DataFieldMaxValueIncrementer getIncrementer() {
		return incrementer;
	}

	public void setIncrementer(DataFieldMaxValueIncrementer incrementer) {
		this.incrementer = incrementer;
	}

}
