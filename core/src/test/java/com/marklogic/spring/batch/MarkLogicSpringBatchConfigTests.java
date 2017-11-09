package com.marklogic.spring.batch;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Test;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.ext.helper.DatabaseClientProvider;

public class MarkLogicSpringBatchConfigTests extends AbstractJobRepositoryTest {
	

	
	@Test
	public void isVersionOptimalUpdatePolicyTest() {

		// create server config manager
		ServerConfigurationManager configMgr = getClient().newServerConfigManager();

		// read the server config from the database
		configMgr.readConfiguration();

		assertEquals(UpdatePolicy.VERSION_REQUIRED, configMgr.getUpdatePolicy());

		// release the client
		//client.release();
	}

}
