package com.marklogic.spring.batch.core.repository.dao;

import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;

public class AbstractMarkLogicBatchMetadataDao implements org.springframework.beans.factory.InitializingBean {
	
	protected DatabaseClient databaseClient;

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

}
