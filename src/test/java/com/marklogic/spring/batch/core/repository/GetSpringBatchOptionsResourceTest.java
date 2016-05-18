package com.marklogic.spring.batch.core.repository;

import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.junit.Fragment;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
		com.marklogic.junit.spring.BasicTestConfig.class
})
public class GetSpringBatchOptionsResourceTest extends AbstractSpringTest {
	
	@Autowired
	ApplicationContext ctx;
	
	@Test
	public void getSpringBatchOptionsTest() throws Exception {
		Resource options = ctx.getResource("classpath:/options/spring-batch-options.xml");
		InputStreamHandle handle = new InputStreamHandle(options.getInputStream());
		Fragment xml = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
		xml.assertElementExists("//search:transform-results");
		handle.close();
	}

}
