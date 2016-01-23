package com.marklogic.client.spring.batch;

import org.springframework.test.context.ContextConfiguration;

import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.SpringBatchConfig.class, com.marklogic.client.spring.DatabaseConfig.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {
	
	protected SpringBatchNamespaceProvider nsProvider;
	
	public AbstractSpringBatchTest() {
		super();
		nsProvider = new SpringBatchNamespaceProvider();
	}
	
	 @Override
	 protected NamespaceProvider getNamespaceProvider() {
		 return nsProvider;
	 }

}
