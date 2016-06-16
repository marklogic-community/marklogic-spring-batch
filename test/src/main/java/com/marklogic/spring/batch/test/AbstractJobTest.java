package com.marklogic.spring.batch.test;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.Main;
import com.marklogic.spring.batch.Options;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any marklogic-spring-batch test that needs to run a job. Uses the Main program
 * to collect command line arguments and run a job.
 */
@ContextConfiguration(classes = {JobTestConfig.class})
public abstract class AbstractJobTest extends AbstractSpringTest {

    @Autowired
    private BasicConfig testConfig;

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

    protected JobExecution runJob(Class<?> clazz, Object... otherArgs) {
        String[] args = buildArgsForConfig(clazz, false, otherArgs);
        return runJob(args);
    }

    protected JobExecution runJobWithMarkLogicJobRepository(Class<?> clazz, Object... otherArgs) {
        String[] args = buildArgsForConfig(clazz, true, otherArgs);
        return runJob(args);
    }

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

    protected String arg(String name) {
        return "--" + name;
    }

    protected String[] buildArgsForConfig(Class<?> configClass, boolean enableMarkLogicJobRepository, Object... otherArgs) {
        List<String> args = new ArrayList<>();
        args.add(arg(Options.HOST));
        args.add(testConfig.getMlHost());
        args.add(arg(Options.PORT));
        args.add(testConfig.getMlRestPort().toString());
        args.add(arg(Options.USERNAME));
        args.add(testConfig.getMlUsername());
        args.add(arg(Options.PASSWORD));
        args.add(testConfig.getMlPassword());
        if (enableMarkLogicJobRepository) {
            args.add(arg(Options.JOB_REPOSITORY_HOST));
            args.add(testConfig.getMlHost());
            args.add(arg(Options.JOB_REPOSITORY_PORT));
            args.add(testConfig.getMlRestPort().toString());
            args.add(arg(Options.JOB_REPOSITORY_USERNAME));
            args.add(testConfig.getMlUsername());
            args.add(arg(Options.JOB_REPOSITORY_PASSWORD));
            args.add(testConfig.getMlPassword());
        }
        args.add(arg(Options.CONFIG));
        args.add(configClass.getName());
        for (Object arg : otherArgs) {
            args.add(arg.toString());
        }
        return args.toArray(new String[]{});
    }
}
