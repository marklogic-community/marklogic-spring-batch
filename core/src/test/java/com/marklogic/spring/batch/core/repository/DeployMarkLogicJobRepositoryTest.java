package com.marklogic.spring.batch.core.repository;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class DeployMarkLogicJobRepositoryTest {

    AppConfig appConfig;
    MarkLogicSimpleJobRepositoryAppDeployer deployer;

    @Before
    public void undeployApplication() {
        appConfig = new AppConfig();
        try {
            appConfig.setConfigDir(new ConfigDir(new ClassPathResource("ml-config").getFile()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        appConfig.setName("spring-batch");
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig("oscar", 8200, "admin", "admin");
        deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.undeploy(appConfig);
    }

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        deployer.deploy(appConfig);
    }
}
