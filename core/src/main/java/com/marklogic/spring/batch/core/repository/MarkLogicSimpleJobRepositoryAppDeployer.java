package com.marklogic.spring.batch.core.repository;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.StringHandle;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.databases.DatabaseManager;
import com.marklogic.mgmt.security.RoleManager;

import java.util.List;

/**
 * Created by sstafford on 6/6/2016.
 */
public class MarkLogicSimpleJobRepositoryAppDeployer extends LoggingObject {

    private List<Command> commands;
    private MarkLogicSimpleJobRepositoryConfig config;

    public MarkLogicSimpleJobRepositoryAppDeployer(MarkLogicSimpleJobRepositoryConfig config) {
        this.config = config;
    }

    public void deploy(String host, int port) {
        config.getRestApi(port).save();

        DatabaseManager dbMgr = new DatabaseManager(config.getManageClient());
        dbMgr.save(config.getDatabase());

        for (String role : config.getRoles()) {
            RoleManager roleMgr = new RoleManager(config.getManageClient());
            roleMgr.save(role);
        }

        for (User user : config.getUsers()) {
            user.save();
        }

        config.getProtectedCollection().save();

        AppConfig appConfig = new AppConfig();
        appConfig.setHost(host);
        appConfig.setRestPort(port);
        appConfig.setRestAdminUsername(config.getManageClient().getManageConfig().getAdminUsername());
        appConfig.setRestAdminPassword(config.getManageClient().getManageConfig().getAdminPassword());
        DatabaseClient client = appConfig.newDatabaseClient();
        ServerConfigurationManager serverConfigMgr = client.newServerConfigManager();

        //Set rest properties
        serverConfigMgr.readConfiguration();
        serverConfigMgr.setQueryValidation(true);
        serverConfigMgr.setDefaultDocumentReadTransformAll(false);
        serverConfigMgr.setUpdatePolicy(ServerConfigurationManager.UpdatePolicy.VERSION_OPTIONAL);
        serverConfigMgr.setQueryOptionValidation(true);
        serverConfigMgr.writeConfiguration();

        //Set Query Options
        QueryOptionsManager qoManager = serverConfigMgr.newQueryOptionsManager();
        qoManager.writeOptions("spring-batch", new StringHandle(config.getSpringBatchOptions()));
        client.release();
    }

    public void undeploy(String host, int port) {
        for (User user : config.getUsers()) {
            user.delete();
        }

        for (String role : config.getRoles()) {
            RoleManager roleMgr = new RoleManager(config.getManageClient());
            roleMgr.delete(role);
        }

        config.getProtectedCollection().delete();

        config.getRestApi(port).delete();
    }
}
