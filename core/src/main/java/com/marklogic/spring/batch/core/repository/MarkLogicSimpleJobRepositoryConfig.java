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

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public ManageClient getManageClient() {
        return manageClient;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    private AppConfig appConfig;
    private ManageClient manageClient;
    private AdminManager adminManager;


    public MarkLogicSimpleJobRepositoryConfig(String host, int port, String adminUsername, String adminPassword) {

        appConfig = new AppConfig();
        try {
            appConfig.setConfigDir(new ConfigDir(new ClassPathResource("ml-config").getFile()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        appConfig.setName("spring-batch");

        ManageConfig manageConfig = new ManageConfig(host, 8002, adminUsername, adminPassword);
        manageClient = new ManageClient(manageConfig);

        AdminConfig adminConfig = new AdminConfig(host, 8001, adminUsername, adminPassword);
        adminManager = new AdminManager(adminConfig);
    }

}
