package com.marklogic.spring.batch;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.config.ConfigTypeFilter;
import com.marklogic.spring.batch.config.support.OptionParserConfigurer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.JOptCommandLinePropertySource;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Main program for running marklogic-spring-batch. Mimicking mlcp args where possible.
 * <p>
 * This is designed so that if an application isn't happy with it, it can at least be subclassed and methods can be
 * overridden to adjust its behavior, thus creating a new command line program.
 */
public class Main extends LoggingObject {

    public static void main(String[] args) throws Exception {
        new Main().runJob(args);
    }

    /**
     * Parses the arguments and figures out what job to run, launching a Spring container if needed.
     *
     * @param args
     * @throws Exception
     */
    public JobExecution runJob(String[] args) throws Exception {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);
        if (options.has(Options.HELP)) {
            printHelp(parser, options, args);
        }
        else if (options.has(Options.LIST)) {
            StringBuilder sb = new StringBuilder();
            listConfigs(options, sb);
            System.out.println(sb.toString());
        }
        else {
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
        return null;
    }

    /**
     * List Spring Configuration classes on the classpath. This uses the optional BASE_PACKAGE option to constrain
     * the classpath. That option could be used in the future too for supporting selecting a config by short class name
     * as opposed to the fully-qualified class name.
     *
     * @param options
     */
    protected void listConfigs(OptionSet options, StringBuilder builder) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Configuration.class));
        scanner.addExcludeFilter(new ConfigTypeFilter());
        String basePackage = options.has(Options.BASE_PACKAGE) ? options.valueOf(Options.BASE_PACKAGE).toString() : "*";
        Set<BeanDefinition> set = scanner.findCandidateComponents(basePackage);
        if (set.isEmpty()) {
            builder.append("No classes with the Spring Configuration annotation were found on the classpath");
        }
        else {
            builder.append("Listing Spring Configuration classes:\n");
            for (BeanDefinition def : set) {
                builder.append(def.getBeanClassName() + "\n");
            }
        }
    }

    protected void printHelp(OptionParser parser, OptionSet options, String[] args) throws IOException {
        System.out.println("General options:");
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

    /**
     * Construct a JOpt OptionParser. A subclass can override this method to add support for its own custom
     * command line options.
     *
     * @return
     */
    protected OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("h", Options.HELP), "Show help").forHelp();
        parser.accepts(Options.HOST, "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts(Options.PORT, "Port number of the destination MarkLogic Server. There should be an XDBC App Server on this port. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.USERNAME, "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.PASSWORD, "The password for the MarkLogic user").withRequiredArg();
        parser.accepts(Options.DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();
        parser.accepts(Options.AUTHENTICATION, "The authentication to use for the app server on the given port").withRequiredArg();

        parser.accepts(Options.LIST, "List all of the Spring Configuration classes on the classpath");
        parser.accepts(Options.BASE_PACKAGE, "The optional base package to use when using --list to find Spring Configuration classes").withRequiredArg();

        parser.accepts(Options.CONFIG, "The fully qualified classname of the Spring Configuration class to register").withRequiredArg();
        parser.accepts(Options.JOB, "The name of the Spring Batch Job bean to run").withRequiredArg();
        parser.accepts(Options.CHUNK_SIZE, "The Spring Batch chunk size").withRequiredArg();

        parser.accepts(Options.JOB_REPOSITORY_NAME, "Name of the REST API server for the MarkLogic JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_DATABASE, "The name of the JobRepository database. Default: The database associated with the destination App Server identified by -jrHost and -jrPort.").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_AUTHENTICATION, "The authentication to use for the app server on the given JobRepository port").withRequiredArg();

        parser.accepts(Options.DEPLOY, "Include this parameter to deploy a MarkLogicJobRepository.  Requires the jr_host, jr_port, jr_username, and jr_password parameters");
        parser.accepts(Options.UNDEPLOY, "Include this parameter to undeploy a MarkLogicJobRepository.  Requires the jr_host, jr_port, jr_username, and jr_password parameters");

        parser.accepts(Options.JDBC_DRIVER, "Driver class name for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_URL, "URL for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_USERNAME, "User for connecting to a relational database").withRequiredArg();
        parser.accepts(Options.JDBC_PASSWORD, "Password for connecting to a relational database").withRequiredArg();

        parser.accepts(Options.OPTIONS_FILE, "Path to a Java-style properties file that defines additional options").withRequiredArg();

        parser.allowsUnrecognizedOptions();
        return parser;
    }

    /**
     * Parse the args and return an OptionSet. Includes support for reading options from a file.
     *
     * @param parser
     * @param args
     * @return
     */
    protected OptionSet parseOptions(OptionParser parser, String... args) {
        OptionSet options = parser.parse(args);
        if (options.has(Options.OPTIONS_FILE)) {
            String path = options.valueOf(Options.OPTIONS_FILE).toString();
            Properties props = new Properties();
            try {
                props.load(new FileReader(new File(path)));
            } catch (IOException e) {
                throw new RuntimeException("Unable to read options from properties file: " + path + "; cause: " + e.getMessage(), e);
            }

            List<String> list = new ArrayList<>();
            for (String arg : args) {
                list.add(arg);
            }
            for (String key : props.stringPropertyNames()) {
                list.add(key);
                list.add(props.getProperty(key));
            }
            return parser.parse(list.toArray(new String[]{}));
        } else {
            return options;
        }
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
     * Register any default Spring config classes.
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

        if (configClass != null) {
            try {
                ctx.register(Class.forName(configClass));
            } catch (Exception e) {
                throw new RuntimeException("Unable to register config for class: " + configClass, e);
            }
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
