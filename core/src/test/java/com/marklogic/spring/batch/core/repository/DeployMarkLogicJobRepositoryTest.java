package com.marklogic.spring.batch.core.repository;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.User;
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

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        apiMgr = new RestApiManager(manageClient);
        Assert.assertFalse(apiMgr.restApiServerExists("spring-batch"));
        Assert.assertFalse(config.getProtectedCollection().exists());
        roleMgr = new RoleManager(manageClient);

        Assert.assertFalse(roleMgr.exists("spring-batch-reader"));
        Assert.assertFalse(roleMgr.exists("spring-batch-admin"));
        Assert.assertFalse(roleMgr.exists("spring-batch-test"));
        
        for (User user : config.getUsers()) {
            Assert.assertFalse(user.exists());
        }
    }

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        int port = databaseClient.getPort();
        deployer.deploy(databaseClient.getHost(), port);
        Assert.assertTrue(apiMgr.restApiServerExists("spring-batch"));

        Assert.assertTrue(roleMgr.exists("spring-batch-reader"));
        Assert.assertTrue(roleMgr.exists("spring-batch-admin"));
        Assert.assertTrue(roleMgr.exists("spring-batch-test"));

        Assert.assertTrue(config.getProtectedCollection().exists());

        for (User user : config.getUsers()) {
            Assert.assertTrue(user.exists());
        }
    }
}
