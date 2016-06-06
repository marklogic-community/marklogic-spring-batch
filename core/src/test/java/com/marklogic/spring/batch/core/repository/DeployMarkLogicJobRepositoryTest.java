package com.marklogic.spring.batch.core.repository;

import com.marklogic.mgmt.restapis.RestApiManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeployMarkLogicJobRepositoryTest {

    MarkLogicSimpleJobRepositoryConfig config;
    MarkLogicSimpleJobRepositoryAppDeployer deployer;
    RestApiManager apiMgr;

    @Before
    public void undeployApplication() {
        config = new MarkLogicSimpleJobRepositoryConfig("oscar", 8200, "admin", "admin");
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
