package com.marklogic.spring.batch;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

public class ListConfigsTest extends AbstractSpringBatchTest {

    private Main main = new Main();

    @Test
    public void test() throws Exception {
        OptionParser parser = main.buildOptionParser();
        OptionSet options = parser.parse(new String[]{"--list-configs", "--base-package", "com.marklogic.spring.batch"});

        StringBuilder sb = new StringBuilder();
        main.listConfigs(options, sb);

        String list = sb.toString();
        assertTrue("MainConfig is expected to be on the classpath", list.contains(MainConfig.class.getName()));
        assertTrue("Our test config below is expected to be on the classpath", list.contains(TestConfigForListConfigTest.class.getName()));
    }

    @Configuration
    public static class TestConfigForListConfigTest {

    }
}
