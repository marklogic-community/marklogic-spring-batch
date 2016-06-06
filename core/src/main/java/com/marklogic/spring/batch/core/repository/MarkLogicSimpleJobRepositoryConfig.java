package com.marklogic.spring.batch.core.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

public class MarkLogicSimpleJobRepositoryConfig {

    AppConfig appConfig;
    ManageConfig manageConfig;
    ManageClient manageClient;
    AdminManager adminManager;


    public MarkLogicSimpleJobRepositoryConfig(String host, int port, String username, String password) {

        appConfig = new AppConfig();
        appConfig.setConfigDir(new ConfigDir(new File("resources/ml-config")));
        appConfig.setName("spring-batch");

        manageConfig = new ManageConfig(host, 8002, username, password);
        manageClient = new ManageClient(manageConfig);

        AdminConfig adminConfig = new AdminConfig(host, 8001, username, password);
        adminManager = new AdminManager(adminConfig);
    }

}
