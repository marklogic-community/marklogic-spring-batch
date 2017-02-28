package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
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
    private DatabaseClientConfig databaseClientConfig;

    @Test
    public void withDatabaseAndAuthentication() throws Exception {
        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(buildArgs("marklogic-spring-batch-test-content", "digest"));
        ConfigurableApplicationContext ctx = main.buildApplicationContext(options);
        DatabaseClientProvider provider = ctx.getBean("databaseClientProvider", DatabaseClientProvider.class);
        DatabaseClient client = provider.getDatabaseClient();
        String response = client.newServerEval().xquery("fn:current-dateTime()").evalAs(String.class);
        assertNotNull("Just verifying that we're able to make a connection successfully", response);
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
        OptionSet options = parser.parse(buildArgs(databaseClientConfig.getDatabase() + "-content", "badauth"));
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
        args.add(databaseClientConfig.getHost());
        args.add(arg(Options.PORT));
        args.add(String.valueOf(databaseClientConfig.getPort()));
        args.add(arg(Options.USERNAME));
        args.add(databaseClientConfig.getUsername());
        args.add(arg(Options.PASSWORD));
        args.add(databaseClientConfig.getPassword());
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
