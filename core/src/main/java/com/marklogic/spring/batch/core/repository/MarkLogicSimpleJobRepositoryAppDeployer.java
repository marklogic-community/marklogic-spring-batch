package com.marklogic.spring.batch.core.repository;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
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
        super(config.getManageClient(), config.getAdminManager());
        this.config = config;
    }

    @Override
    protected List<Command> getCommands() {
        List<Command> commands = new ArrayList<Command>();

        DeployRestApiServersCommand restApiCommand = new DeployRestApiServersCommand("rest-api.json");
        commands.add(restApiCommand);

        DeployDatabaseCommand dbCommand = new DeployDatabaseCommand("content-database.json");
        commands.add(dbCommand);

        DeployRolesCommand rolesCommand = new DeployRolesCommand();
        commands.add(rolesCommand);

        DeployUsersCommand usersCommand = new DeployUsersCommand();
        commands.add(usersCommand);

        DeployProtectedCollectionsCommand protectedCollectionsCommand = new DeployProtectedCollectionsCommand();
        commands.add(protectedCollectionsCommand);

        return commands;
    }
}
