package com.marklogic.spring.batch.core.repository;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.mgmt.restapis.RestApiManager;
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

    @Before
    public void undeployApplication() {
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        config = new MarkLogicSimpleJobRepositoryConfig(
                databaseClient.getHost(),
                databaseClient.getPort(),
                "admin",
                "admin");
        deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.undeploy(config.getAppConfig());

        apiMgr = new RestApiManager(config.getManageClient());
        Assert.assertFalse(apiMgr.restApiServerExists("spring-batch"));
    }

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        deployer.deploy(config.getAppConfig());
        Assert.assertTrue(apiMgr.restApiServerExists("spring-batch"));
    }
}
