package com.marklogic.client.spring.batch.core.repository;

public interface MarkLogicSpringBatchRepository {

	public final String SPRING_BATCH_DIR = "/projects.spring.io/spring-batch/";
	public final String COLLECTION_JOB_EXECUTION = "http://marklogic.com/spring-batch/job-execution";
	public final String COLLETION_JOB_INSTANCE =  "http://marklogic.com/spring-batch/job-instance";
}
