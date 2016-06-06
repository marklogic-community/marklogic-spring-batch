package com.marklogic.spring.batch;

import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryAppDeployer;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Created by sstafford on 6/6/2016.
 */
public class DeployMarkLogicJobRepositoryMain {

    public static void main(String[] args) {
        new DeployMarkLogicJobRepositoryMain().deploy(args);
    }

    public void deploy(String[] args) {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);

        MarkLogicSimpleJobRepositoryConfig config =
                new MarkLogicSimpleJobRepositoryConfig(
                        options.valueOf(Options.JOB_REPOSITORY_HOST).toString(),
                        Integer.parseInt(options.valueOf(Options.JOB_REPOSITORY_PORT).toString()),
                        options.valueOf(Options.JOB_REPOSITORY_USERNAME).toString(),
                        options.valueOf(Options.JOB_REPOSITORY_PASSWORD).toString());
        MarkLogicSimpleJobRepositoryAppDeployer appDeployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        if (options.has("undeploy")) {
            appDeployer.undeploy(config.getAppConfig());
        } else {
            appDeployer.deploy(config.getAppConfig());
        }
    }

    protected OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();
        parser.accepts("undeploy", "true - optional parameter to undeploy the MarkLogicJobRepository");
        return parser;
    }

}
