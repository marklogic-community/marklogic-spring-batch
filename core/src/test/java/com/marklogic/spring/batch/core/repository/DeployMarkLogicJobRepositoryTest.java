package com.marklogic.spring.batch.core.repository;

import org.junit.Before;
import org.junit.Test;

public class DeployMarkLogicJobRepositoryTest {

    MarkLogicSimpleJobRepositoryConfig config;
    MarkLogicSimpleJobRepositoryAppDeployer deployer;

    @Before
    public void undeployApplication() {
        config = new MarkLogicSimpleJobRepositoryConfig("oscar", 8200, "admin", "admin");
        deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.undeploy(config.getAppConfig());
    }

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        deployer.deploy(config.getAppConfig());

    }
}
