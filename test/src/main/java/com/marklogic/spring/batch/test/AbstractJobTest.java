package com.marklogic.spring.batch.test;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.junit.spring.BasicTestConfig;
import com.marklogic.spring.batch.Main;
import com.marklogic.spring.batch.Options;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any marklogic-spring-batch test that needs to run a job. Uses the Main program
 * to collect command line arguments and run a job.
 * <p>
 * Key note - this does not specify a particular Spring configuration class - you are free to use
 * any Spring class that you want. But the buildArgsForConfig method assumes that it can find either
 * an instance of com.marklogic.client.spring.BasicConfig or com.marklogic.junit.spring.BasicTestConfig
 * so that it knows what ML connection arguments to construct. If you don't use either of those,
 * you'll need to override the getMlConnectionArgs method.
 */
public abstract class AbstractJobTest extends AbstractSpringTest {

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

    /**
     * Assumes that the given config class defines a single Job bean, and runs it.
     *
     * @param clazz
     * @param otherArgs arguments to be passed in, in addition to the ML connection arguments
     * @return
     */
    protected JobExecution runJob(Class<?> clazz, Object... otherArgs) {
        String[] args = buildArgsForConfig(clazz, false, otherArgs);
        return runJob(args);
    }

    /**
     * Assumes that the given config class defines a single Job bean, and runs it. Passes in the
     * ML connection arguments for the job repository connection parameters, thus enabling the MarkLogic
     * JobRepository implementation.
     *
     * @param clazz
     * @param otherArgs
     * @return
     */
    protected JobExecution runJobWithMarkLogicJobRepository(Class<?> clazz, Object... otherArgs) {
        String[] args = buildArgsForConfig(clazz, true, otherArgs);
        return runJob(args);
    }

    /**
     * Run a job with the given arguments.
     *
     * @param args
     * @return
     */
    protected JobExecution runJob(String[] args) {
        JobExecution exec = null;
        try {
            exec = new Main().runJob(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(BatchStatus.COMPLETED, exec.getStatus());
        return exec;
    }

    /**
     * @param configClass
     * @param enableMarkLogicJobRepository
     * @param otherArgs
     * @return an array of args that can be passed into the marklogic-spring-batch Main program. Assumes
     * that only host, port, username, and password are needed. If that's not the case, you can always pass
     * in additional arguments via the "otherArgs" parameter.
     */
    protected String[] buildArgsForConfig(Class<?> configClass, boolean enableMarkLogicJobRepository, Object... otherArgs) {
        String[] connectionArgs = getMlConnectionArgs();
        String host = connectionArgs[0];
        String port = connectionArgs[1];
        String username = connectionArgs[2];
        String password = connectionArgs[3];

        List<String> args = new ArrayList<>();
        args.add(arg(Options.HOST));
        args.add(host);
        args.add(arg(Options.PORT));
        args.add(port);
        args.add(arg(Options.USERNAME));
        args.add(username);
        args.add(arg(Options.PASSWORD));
        args.add(password);
        if (enableMarkLogicJobRepository) {
            args.add(arg(Options.JOB_REPOSITORY_HOST));
            args.add(host);
            args.add(arg(Options.JOB_REPOSITORY_PORT));
            args.add(port);
            args.add(arg(Options.JOB_REPOSITORY_USERNAME));
            args.add(username);
            args.add(arg(Options.JOB_REPOSITORY_PASSWORD));
            args.add(password);
        }
        args.add(arg(Options.CONFIG));
        args.add(configClass.getName());
        for (Object arg : otherArgs) {
            args.add(arg.toString());
        }
        return args.toArray(new String[]{});
    }

    /**
     * @param name
     * @return assumes that the argument should use the "long" form supported by joptsimple
     */
    protected String arg(String name) {
        return "--" + name;
    }

    /**
     * @return an array of 4 arguments - host, port, username, and password. These are used
     * by buildArgsForConfig. You can override this in case your class does not use BasicConfig or
     * BasicTestConfig.
     */
    protected String[] getMlConnectionArgs() {
        ApplicationContext ctx = getApplicationContext();
        BasicConfig config = ctx.getBean(BasicConfig.class);
        String mlHost = config.getMlHost();
        String mlUsername = config.getMlUsername();
        String mlPassword = config.getMlPassword();
        Integer port = config.getMlRestPort();
        try {
            BasicTestConfig testConfig = ctx.getBean(BasicTestConfig.class);
            port = testConfig.getMlTestRestPort();
        } catch (NoSuchBeanDefinitionException ex) {
            // ignore
        }
        return new String[]{mlHost, port.toString(), mlUsername, mlPassword};
    }
}
