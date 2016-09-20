package com.marklogic.spring.batch;

import com.marklogic.client.spring.BasicConfig;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

public class ListConfigsTest extends AbstractSpringBatchTest {

	private Main main = new Main();

	@Test
	public void test() throws Exception {
		OptionParser parser = main.buildOptionParser();
		OptionSet options = parser.parse(new String[]{"--list", "--base-package", "com.marklogic.spring.batch"});

		StringBuilder sb = new StringBuilder();
		main.listConfigs(options, sb);

		String list = sb.toString();
		System.out.println(list);

		assertTrue("Our test config below is expected to be on the classpath", list.contains(TestConfigForListConfigTest.class.getName()));
		assertFalse("MainConfig should have been filtered out", list.contains(MainConfig.class.getName()));
		assertFalse("BasicConfig should have been filtered out", list.contains(BasicConfig.class.getName()));
	}

	@Configuration
	public static class TestConfigForListConfigTest {

	}
}
