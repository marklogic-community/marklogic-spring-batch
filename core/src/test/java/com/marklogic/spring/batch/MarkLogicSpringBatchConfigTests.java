package com.marklogic.spring.batch;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Test;

import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;

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
