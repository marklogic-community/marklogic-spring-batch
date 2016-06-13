package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MainConfigTest extends AbstractSpringBatchTest {

    @Autowired
    private BasicConfig testConfig;

    @Test
    public void withDatabaseAndAuthentication() throws Exception {
        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(buildArgs(testConfig.getMlAppName() + "-content", "digest"));
        ConfigurableApplicationContext ctx = main.buildApplicationContext(options);
        DatabaseClientProvider provider = ctx.getBean("databaseClientProvider", DatabaseClientProvider.class);
        DatabaseClient client = provider.getDatabaseClient();
        try {
            String response = client.newServerEval().xquery("fn:current-dateTime()").evalAs(String.class);
            assertNotNull("Just verifying that we're able to make a connection successfully", response);
        } finally {
            client.release();
            ctx.close();
        }
    }

    @Test
    public void withInvalidDatabase() throws Exception {
        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(buildArgs("unrecognized-database-name", "digest"));
        ConfigurableApplicationContext ctx = main.buildApplicationContext(options);
        DatabaseClientProvider provider = ctx.getBean("databaseClientProvider", DatabaseClientProvider.class);
        DatabaseClient client = provider.getDatabaseClient();
        try {
            client.newServerEval().xquery("fn:current-dateTime()").evalAs(String.class);
            fail("Expected the call to fail because the connection is for an invalid database");
        } catch (FailedRequestException fre) {
            assertTrue(fre.getMessage().contains("No such database unrecognized-database-name"));
        }
    }

    @Test
    public void withInvalidAuthentication() throws Exception {
        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(buildArgs(testConfig.getMlAppName() + "-content", "badauth"));
        try {
            main.buildApplicationContext(options);
            fail("Expected Spring container construction to fail because of an invalid authentication value");
        } catch (BeanCreationException bce) {
            assertTrue(bce.getMessage().contains("Error creating bean with name 'databaseClientProvider'"));
        }
    }

    private String[] buildArgs(String database, String auth) {
        List<String> args = new ArrayList<>();
        args.add(arg(Options.HOST));
        args.add(testConfig.getMlHost());
        args.add(arg(Options.PORT));
        args.add(testConfig.getMlRestPort().toString());
        args.add(arg(Options.USERNAME));
        args.add(testConfig.getMlUsername());
        args.add(arg(Options.PASSWORD));
        args.add(testConfig.getMlPassword());
        args.add(arg(Options.DATABASE));
        args.add(database);
        args.add(arg(Options.AUTHENTICATION));
        args.add(auth);
        return args.toArray(new String[]{});
    }

    private String arg(String name) {
        return "--" + name;
    }
}
