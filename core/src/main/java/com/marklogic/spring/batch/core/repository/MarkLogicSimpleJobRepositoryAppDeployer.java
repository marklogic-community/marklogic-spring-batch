package com.marklogic.spring.batch.core.repository;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.impl.AbstractAppDeployer;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.admin.AdminManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sstafford on 6/6/2016.
 */
public class MarkLogicSimpleJobRepositoryAppDeployer extends AbstractAppDeployer {

    private List<Command> commands;
    private MarkLogicSimpleJobRepositoryConfig config;

    public MarkLogicSimpleJobRepositoryAppDeployer(MarkLogicSimpleJobRepositoryConfig config) {
        super(config.manageClient, config.adminManager);
        this.config = config;
    }

    @Override
    protected List<Command> getCommands() {
        List<Command> commands = new ArrayList<Command>();

        DeployRestApiServersCommand restApiCommand = new DeployRestApiServersCommand("rest-api.json");
        commands.add(restApiCommand);

        DeployDatabaseCommand dbCommand = new DeployDatabaseCommand("content-database.json");
        commands.add(dbCommand);

        return commands;
    }
}
