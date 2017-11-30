package com.marklogic.spring.batch;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;

public class DeployMarkLogicJobRepository {

    private String HELP = "help";
    private String GROUP = "group";
    private String NAME = "name";
    private String HOST = "host";
    private String PORT = "port";
    private String USERNAME = "username";
    private String PASSWORD = "password";
    private String DATABASE = "database";
    private String AUTHENTICATION = "auth";
    private String UNDEPLOY = "undeploy";

    public static void main(String args[]) throws Exception {
        new DeployMarkLogicJobRepository().run(args);
    }

    public void run(String[] args) throws Exception {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);
        if (options.has(HELP)) {
            parser.printHelpOn(System.out);
        } else if (options.has(UNDEPLOY)) {
            undeployMarkLogicJobRepository(options);
        } else {
            deployMarkLogicJobRepository(options);
        }
    }

    protected void deployMarkLogicJobRepository(OptionSet options) {
        String name = options.valueOf(NAME).toString();
        String host = options.valueOf(HOST).toString();
        String group = options.valueOf(GROUP).toString();
        buildAppDeployer(options).deploy(name, host, Integer.parseInt(options.valueOf(PORT).toString()), group);
    }

    protected void undeployMarkLogicJobRepository(OptionSet options) {
        String name = options.valueOf(NAME).toString();
        String host = options.valueOf(HOST).toString();
        String group = options.valueOf(GROUP).toString();
        buildAppDeployer(options).undeploy(name, host, Integer.parseInt(options.valueOf(PORT).toString()), group);
    }

    protected MarkLogicSimpleJobRepositoryAppDeployer buildAppDeployer(OptionSet options) {
        String host = options.valueOf(HOST).toString();
        ManageConfig manageConfig = new ManageConfig(
                host, 8002,
                options.valueOf(USERNAME).toString(),
                options.valueOf(PASSWORD).toString());
        ManageClient manageClient = new ManageClient(manageConfig);
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig(manageClient);
        return new MarkLogicSimpleJobRepositoryAppDeployer(config);
    }

    protected OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("h", HELP), "Show help").forHelp();
        parser.accepts(NAME, "Name of the MarkLogic Job Repository").withRequiredArg().defaultsTo("mlJobRepo");
        parser.accepts(GROUP, "Group where the destination host lives").withRequiredArg().defaultsTo("Default");
        parser.accepts(HOST, "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(PORT, "Port number of the destination MarkLogic Server. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8015);
        parser.accepts(USERNAME, "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(PASSWORD, "The password for the MarkLogic user").withRequiredArg().defaultsTo("admin");
        parser.accepts(DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();
        parser.accepts(AUTHENTICATION, "The authentication to use for the app server on the given port").withRequiredArg();
        parser.accepts(UNDEPLOY, "Include this parameter to undeploy a MarkLogicJobRepository.  Requires the host, port, username, and password parameters");
        parser.allowsUnrecognizedOptions();
        return parser;
    }

}
