package com.marklogic.spring.batch.core.repository;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.restapis.RestApiManager;
import com.marklogic.mgmt.security.RoleManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BasicConfig.class})
public class DeployMarkLogicJobRepositoryTest {

    MarkLogicSimpleJobRepositoryConfig config;
    MarkLogicSimpleJobRepositoryAppDeployer deployer;
    RestApiManager apiMgr;

    @Autowired
    DatabaseClientProvider databaseClientProvider;

    RoleManager roleMgr;

    @Before
    public void undeployApplication() {
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        int port = databaseClient.getPort();
        ManageConfig manageConfig = new ManageConfig(databaseClient.getHost(), 8002, "admin", "admin");
        ManageClient manageClient = new ManageClient(manageConfig);
        config = new MarkLogicSimpleJobRepositoryConfig(manageClient);
        deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.undeploy(databaseClient.getHost(), port);

        apiMgr = new RestApiManager(manageClient);
        Assert.assertFalse(apiMgr.restApiServerExists("spring-batch"));
        roleMgr = new RoleManager(manageClient);
    }

    @Test
    public void deployMarkLogicJobRepositoryTest() {

        Assert.assertFalse(roleMgr.exists("spring-batch-reader"));
        Assert.assertFalse(roleMgr.exists("spring-batch-admin"));
        Assert.assertFalse(roleMgr.exists("spring-batch-test"));

        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        int port = databaseClient.getPort();
        deployer.deploy(databaseClient.getHost(), port);
        Assert.assertTrue(apiMgr.restApiServerExists("spring-batch"));

        Assert.assertTrue(roleMgr.exists("spring-batch-reader"));
        Assert.assertTrue(roleMgr.exists("spring-batch-admin"));
        Assert.assertTrue(roleMgr.exists("spring-batch-test"));
    }
}
