package com.marklogic.spring.batch;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryAppDeployer;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepositoryConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.JOptCommandLinePropertySource;

import java.util.List;

/**
 * Main program for running marklogic-spring-batch. Mimicking mlcp args where possible.
 * <p>
 * This is designed so that if an application isn't happy with it, it can at least be subclassed and methods can be
 * overridden to adjust its behavior, thus creating a new command line program.
 */
public class Main extends LoggingObject {

    /**
     * For now, mimicking mlcp args where possible.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        if (args[0].equals(Options.DEPLOY)) {
            main.deployMarkLogicJobRepository(args);
        } else if (args[0].equals(Options.UNDEPLOY)) {
            main.undeployMarkLogicJobRepository(args);
        } else {
            main.runJob(args);
        }
    }

    public void deployMarkLogicJobRepository(String[] args) {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);
        String host = options.valueOf(Options.JOB_REPOSITORY_HOST).toString();
        ManageConfig manageConfig = new ManageConfig(
                host, 8002,
                options.valueOf(Options.JOB_REPOSITORY_USERNAME).toString(),
                options.valueOf(Options.JOB_REPOSITORY_PASSWORD).toString());
        ManageClient manageClient = new ManageClient(manageConfig);
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig(manageClient);
        MarkLogicSimpleJobRepositoryAppDeployer deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.deploy(host, Integer.parseInt(options.valueOf(Options.JOB_REPOSITORY_PORT).toString()));
    }

    public void undeployMarkLogicJobRepository(String[] args) {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);
        String host = options.valueOf(Options.JOB_REPOSITORY_HOST).toString();
        ManageConfig manageConfig = new ManageConfig(
                host, 8002,
                options.valueOf(Options.JOB_REPOSITORY_USERNAME).toString(),
                options.valueOf(Options.JOB_REPOSITORY_PASSWORD).toString());
        ManageClient manageClient = new ManageClient(manageConfig);
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig(manageClient);
        MarkLogicSimpleJobRepositoryAppDeployer deployer = new MarkLogicSimpleJobRepositoryAppDeployer(config);
        deployer.undeploy(host, Integer.parseInt(options.valueOf(Options.JOB_REPOSITORY_PORT).toString()));
    }

    /**
     * Instantiate a Spring container and launch a Spring Batch job based on the given arguments.
     *
     * @param args
     * @throws Exception
     */
    public JobExecution runJob(String[] args) throws Exception {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);
        ConfigurableApplicationContext ctx = buildApplicationContext(options);
        JobParameters params = buildJobParameters(options);
        JobLauncher launcher = getJobLauncher(ctx);
        Job job = getJobToExecute(ctx, options);
        try {
            return launcher.run(job, params);
        } finally {
            ctx.close();
        }
    }

    /**
     * Construct a JOpt OptionParser. A subclass can override this method to add support for its own custom
     * command line options.
     *
     * @return
     */
    protected OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.accepts(Options.HOST, "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(Options.PORT, "Port number of the destination MarkLogic Server. There should be an XDBC App Server on this port. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.USERNAME, "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.PASSWORD, "The password for the MarkLogic user").withRequiredArg();
        parser.accepts(Options.CONFIG, "The fully qualified classname of the Spring Configuration class to register").withRequiredArg();
        parser.accepts(Options.JOB, "The name of the Spring Batch Job bean to run").withRequiredArg();

        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();

        parser.accepts(Options.DEPLOY, "Include this parameter to deploy a MarkLogicJobRepository.  Requires the jrHost, jrPort, jrUsername, and jrPassword parameters");
        parser.accepts(Options.UNDEPLOY, "Include this parameter to undeploy a MarkLogicJobRepository.  Requires the jrHost, jrPort, jrUsername, and jrPassword parameters");

        parser.allowsUnrecognizedOptions();
        return parser;
    }

    /**
     * Build and refresh a Spring application context based on the given JOpt command line options.
     *
     * @param options
     * @return
     * @throws Exception
     */
    protected ConfigurableApplicationContext buildApplicationContext(OptionSet options) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        registerDefaultConfigurations(ctx);
        registerConfigurationsFromOptions(ctx, options);
        ctx.refresh();
        return ctx;
    }

    /**
     * Register any default Spring configuration classes.
     *
     * @param ctx
     */
    protected void registerDefaultConfigurations(AnnotationConfigApplicationContext ctx) {
        ctx.register(MainConfig.class);
    }

    /**
     * Register Spring configurations based on the command line options. Includes adding properties to the
     * Spring environment based on the command line options.
     *
     * @param ctx
     * @param options
     */
    protected void registerConfigurationsFromOptions(AnnotationConfigApplicationContext ctx, OptionSet options) {
        JOptCommandLinePropertySource ps = new JOptCommandLinePropertySource(options);
        String configClass = ps.getProperty(Options.CONFIG);

        try {
            ctx.register(Class.forName(configClass));
        } catch (Exception e) {
            throw new RuntimeException("Unable to register configuration for class: " + configClass, e);
        }

        addOptionsToEnvironment(ctx, ps);
    }

    /**
     * The JOpt options are added to the front of the list of property sources specifically so that
     * the "username" option takes precedence over the built-in "username" property that captures the
     * name of the current OS user. A subclass may wish to change this behavior, and thus this is done
     * in a protected method.
     *
     * @param ctx
     * @param propertySource
     */
    protected void addOptionsToEnvironment(AnnotationConfigApplicationContext ctx, JOptCommandLinePropertySource propertySource) {
        ctx.getEnvironment().getPropertySources().addFirst(propertySource);
    }

    /**
     * Construct Spring Batch job parameters based on the command line options.
     *
     * @param options
     * @return
     */
    protected JobParameters buildJobParameters(OptionSet options) {
        JobParametersBuilder jpb = new JobParametersBuilder();

        /**
         * Treat non-option arguments as job parameters. Thus, recognized options are considered
         * to be necessary for resolving @Value annotations on the given Job class, whereas
         * unrecognized options are considered to be job parameters.
         */
        List<?> nonOptionArgs = options.nonOptionArguments();
        int size = nonOptionArgs.size();
        for (int i = 0; i < size; i++) {
            String name = nonOptionArgs.get(i).toString();
            i++;
            if (i < size) {
                if (name.startsWith("--")) {
                    name = name.substring(2);
                } else if (name.startsWith("-")) {
                    name = name.substring(1);
                }
                String value = nonOptionArgs.get(i).toString();
                jpb.addString(name, value);
            }
        }

        return jpb.toJobParameters();
    }

    /**
     * Return a JobLauncher to use based on the given Spring container.
     *
     * @param ctx
     * @return
     */
    protected JobLauncher getJobLauncher(ApplicationContext ctx) {
        return ctx.getBean(JobLauncher.class);
    }

    /**
     * Determine the Job to execute within the Spring container. If more than one Job bean exists, then the
     * "job" option must be set to the name of one of the Job beans.
     *
     * @param ctx
     * @param options
     * @return
     */
    protected Job getJobToExecute(ApplicationContext ctx, OptionSet options) {
        return options.has(Options.JOB) ?
                ctx.getBean((String) options.valueOf(Options.JOB), Job.class) :
                ctx.getBean(Job.class);
    }
}
