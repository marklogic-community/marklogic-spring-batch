package com.marklogic.spring.batch.core.repository.dao;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.QueryOptionsListHandle;

public abstract class AbstractMarkLogicBatchMetadataDao implements InitializingBean {
	
	@Autowired
	protected ApplicationContext ctx;
	
	protected DatabaseClient databaseClient;
	
	private static final Log logger = LogFactory.getLog(AbstractMarkLogicBatchMetadataDao.class);
	
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
		
		QueryOptionsManager queryOptionsMgr = databaseClient.newServerConfigManager().newQueryOptionsManager();
		Resource options = ctx.getResource("classpath:/options/spring-batch-options.xml");
		InputStreamHandle handle = new InputStreamHandle(options.getInputStream());
		queryOptionsMgr.writeOptions(SEARCH_OPTIONS_NAME, handle);
		logger.info(SEARCH_OPTIONS_NAME + " options loaded");
		QueryOptionsListHandle qolHandle = queryOptionsMgr.optionsList(new QueryOptionsListHandle());
		Set<String> results = qolHandle.getValuesMap().keySet();
		assert(results.contains(SEARCH_OPTIONS_NAME) == true);
	}
	
	public Long generateId() {
		return Math.abs(ThreadLocalRandom.current().nextLong());
	}

}