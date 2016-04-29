package com.marklogic.spring.batch.core.repository.dao;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;

public class AbstractMarkLogicBatchMetadataDao implements org.springframework.beans.factory.InitializingBean {
	
	protected DatabaseClient databaseClient;
	
	protected final long LOWER_RANGE = 0; 
    protected final long UPPER_RANGE = 9999999;

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
