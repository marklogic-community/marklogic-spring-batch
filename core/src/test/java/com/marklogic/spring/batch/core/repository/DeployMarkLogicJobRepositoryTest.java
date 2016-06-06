package com.marklogic.spring.batch.core.repository;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.restapis.RestApiManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DeployMarkLogicJobRepositoryTest {

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        AppConfig appConfig = new AppConfig();
        try {
            appConfig.setConfigDir(new ConfigDir(new ClassPathResource("ml-config").getFile()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        appConfig.setName("spring-batch");
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig("oscar", 8200, "admin", "admin");
        MarkLogicSimpleJobRepositoryAppDeployer deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.deploy(appConfig);
    }
}
