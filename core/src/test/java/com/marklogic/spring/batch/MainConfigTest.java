package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MainConfigTest extends AbstractSpringBatchTest {

    @Autowired
    private BasicConfig testConfig;

    @Test
    public void withDatabaseAndAuthentication() throws Exception {
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
        args.add(testConfig.getMlAppName() + "-content");
        args.add(arg(Options.AUTHENTICATION));
        args.add("digest");

        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(args.toArray(new String[]{}));
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

    private String arg(String name) {
        return "--" + name;
    }
}
