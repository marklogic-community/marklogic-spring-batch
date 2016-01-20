package com.marklogic.client.spring.batch.corb;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.client.spring.batch.AbstractSpringBatchTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.corb.CorbConfig.class } )
public class GetUrisModuleTest extends AbstractSpringBatchTest {
	
	@Autowired
	GetUrisTasklet getUrisTasklet;
	
	@Test
	public void getUrisTest() throws Exception {
		getUrisTasklet.execute(null, null);
	}
}
