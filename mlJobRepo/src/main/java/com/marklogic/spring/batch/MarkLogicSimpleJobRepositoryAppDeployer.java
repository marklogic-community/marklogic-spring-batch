package com.marklogic.spring.batch;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import com.marklogic.mgmt.resource.security.RoleManager;

import java.util.List;

public class MarkLogicSimpleJobRepositoryAppDeployer {

    private List<Command> commands;
    private MarkLogicSimpleJobRepositoryConfig config;

    public MarkLogicSimpleJobRepositoryAppDeployer(MarkLogicSimpleJobRepositoryConfig config) {
        this.config = config;
    }

    /**
     * @param name The name of the JobRepository; affects the name of the REST API server and the content database
     * @param host
     * @param port
     */
    public void deploy(String name, String host, int port) {
        for (String role : config.getRoles()) {
            RoleManager roleMgr = new RoleManager(config.getManageClient());
            roleMgr.save(role);
        }

        for (User user : config.getUsers()) {
            user.save();
        }

        config.getProtectedCollection().save();

        if (new RestApiManager(config.getManageClient()).restApiServerExists(name)) {
            //logger.debug("REST API server with name " + name + " already exists, not creating");
        } else {
            config.getRestApi(name, port).save();
        }

        // Update the database; it's assumed to have the same name as the one created via the REST API call
        DatabaseManager dbMgr = new DatabaseManager(config.getManageClient());
        dbMgr.save(config.getDatabase(name));

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

    public void undeploy(String name, String host, int port) {
        for (User user : config.getUsers()) {
            user.delete();
        }

        for (String role : config.getRoles()) {
            RoleManager roleMgr = new RoleManager(config.getManageClient());
            roleMgr.delete(role);
        }

        config.getProtectedCollection().delete();

        config.getRestApi(name, port).delete(true, true);
    }
}
