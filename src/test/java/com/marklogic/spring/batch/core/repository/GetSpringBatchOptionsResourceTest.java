package com.marklogic.spring.batch.core.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;

public class GetSpringBatchOptionsResourceTest extends AbstractSpringBatchTest {
	
	@Autowired
	ApplicationContext ctx;
	
	@Test
	public void getSpringBatchOptionsTest() throws Exception {
		Resource options = ctx.getResource("classpath:/options/spring-batch-options.xml");
		InputStreamHandle handle = new InputStreamHandle(options.getInputStream());
		Fragment xml = new Fragment(handle.toString(), getNamespaceProvider().getNamespaces());
		xml.assertElementExists("//search:transform-results");
	}
	
		
}
