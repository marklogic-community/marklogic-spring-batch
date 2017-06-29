package com.marklogic.spring.batch;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.spring.batch.config.support.OptionParserConfigurer;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryAppDeployer;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Arrays;

public class DeployMarkLogicJobRepository {

    private String HELP = "help";
    private String NAME = "job-repository";
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
            printHelp(parser, options, args);
        }
        else if (options.has(UNDEPLOY)) {
            undeployMarkLogicJobRepository(options);
        }
        else {
            deployMarkLogicJobRepository(options);
        }
    }

    protected void deployMarkLogicJobRepository(OptionSet options) {
        String name = options.valueOf(NAME).toString();
        String host = options.valueOf(HOST).toString();
        buildAppDeployer(options).deploy(name, host, Integer.parseInt(options.valueOf(Options.PORT).toString()));
    }

    protected void undeployMarkLogicJobRepository(OptionSet options) {
        String name = options.valueOf(NAME).toString();
        String host = options.valueOf(HOST).toString();
        buildAppDeployer(options).undeploy(name, host, Integer.parseInt(options.valueOf(Options.PORT).toString()));
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
        parser.acceptsAll(Arrays.asList("h", Options.HELP), "Show help").forHelp();
        parser.accepts(NAME, "Name of the MarkLogic Job Repository").withOptionalArg().defaultsTo("job-repository");
        parser.accepts(HOST, "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(PORT, "Port number of the destination MarkLogic Server. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8015);
        parser.accepts(USERNAME, "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(PASSWORD, "The password for the MarkLogic user").withRequiredArg().defaultsTo("admin");
        parser.accepts(DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();
        parser.accepts(AUTHENTICATION, "The authentication to use for the app server on the given port").withRequiredArg();
        parser.accepts(UNDEPLOY, "Include this parameter to undeploy a MarkLogicJobRepository.  Requires the jr_host, jr_port, jr_username, and jr_password parameters");
        parser.allowsUnrecognizedOptions();
        return parser;
    }

    protected void printHelp(OptionParser parser, OptionSet options, String[] args) throws IOException {
        parser.printHelpOn(System.out);
        if (options.has(Options.CONFIG)) {
            String config = (String) options.valueOf(Options.CONFIG);
            try {
                Object o = Class.forName(config).newInstance();
                if (o instanceof OptionParserConfigurer) {
                    parser = new OptionParser();
                    ((OptionParserConfigurer) o).configureOptionParser(parser);
                    System.out.println("\nOptions specific to config class: " + config);
                    parser.printHelpOn(System.out);
                }
            } catch (Exception ex) {
                // Ignore, don't try to print options for the config class if we can't create it
            }
        }
    }



}
