package com.marklogic.spring.batch.core;

public interface MarkLogicSpringBatch {
	
	public static final String JOB_NAMESPACE = "http://marklogic.com/spring-batch";
	public static final String JOB_NAMESPACE_PREFIX = "msb";
	
	public static final String JOB_EXECUTION_NAMESPACE = "http://marklogic.com/spring-batch";
	public static final String JOB_EXECUTION_NAMESPACE_PREFIX = "msb";
	
	public static final String JOB_INSTANCE_NAMESPACE = "http://marklogic.com/spring-batch/job-instance";
	public static final String JOB_INSTANCE_NAMESPACE_PREFIX = "inst";
	
	public static final String STEP_EXECUTION_NAMESPACE = "http://marklogic.com/spring-batch/step-execution";
	public static final String STEP_EXECUTION_NAMESPACE_PREFIX = "step";
	
	public static final String JOB_PARAMETER_NAMESPACE = "http://marklogic.com/spring-batch/job-parameter";
	public static final String JOB_PARAMETER_NAMESPACE_PREFIX = "jp";
	
	public static final String EXECUTION_CONTEXT_NAMESPACE = "http://marklogic.com/spring-batch/execution-context";
	public static final String EXECUTION_CONTEXT_NAMESPACE_PREFIX = "ec";
	
	public static final String SPRING_BATCH_DIR = "/projects.spring.io/spring-batch/";
	public static final String COLLECTION_JOB_EXECUTION = "http://marklogic.com/spring-batch/job-execution";
	

}
