package com.marklogic.spring.batch.bind;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;

public class UnmarshallJobExecutionTest extends AbstractSpringBatchTest {
	
	Unmarshaller unmarshaller;
	
	@Before
	public void setup() throws Exception {
		unmarshaller = JAXBContext.newInstance(AdaptedJobParameters.class, AdaptedJobInstance.class).createUnmarshaller();
	}
	
	@Test
	public void unmarshallJobParameters() throws Exception {
		StringReader xml = new StringReader("<msb:jobParameters xmlns:msb=\"http://projects.spring.io/spring-batch\">" + 
						"<msb:jobParameter key=\"stringTest\" type=\"STRING\" identifier=\"true\">Joe Cool</msb:jobParameter>" + 
						"<msb:jobParameter key=\"start\" type=\"DATE\" identifier=\"false\">2016-02-15T21:39:21-0500</msb:jobParameter>" +
						"<msb:jobParameter key=\"longTest\" type=\"LONG\" identifier=\"false\">1239</msb:jobParameter>" +
						"<msb:jobParameter key=\"doubleTest\" type=\"DOUBLE\" identifier=\"false\">1.35</msb:jobParameter>" +
					"</msb:jobParameters>");
		AdaptedJobParameters params = (AdaptedJobParameters)unmarshaller.unmarshal(xml);
		assertEquals(4, params.getParameters().size());
		assertEquals("Joe Cool", params.getParameters().get(0).value);
		assertEquals("2016-02-15T21:39:21-0500", params.getParameters().get(1).value);
		assertEquals("1239", params.getParameters().get(2).value);
		assertEquals("1.35", params.getParameters().get(3).value);
	}
	
	@Test
	public void unmarshallJobInstance() throws Exception {
		StringReader xml = new StringReader("<msb:jobInstance xmlns:msb=\"http://projects.spring.io/spring-batch\">" +
									"<msb:id>123</msb:id>" +
									"<msb:jobName>TestJobInstance</msb:jobName>" + 
									"</msb:jobInstance>");
		AdaptedJobInstance jobInstance = (AdaptedJobInstance)unmarshaller.unmarshal(xml);
		assertEquals(new Long(123L), jobInstance.getId());
		assertEquals("TestJobInstance", jobInstance.getJobName());
		
	}
	
	

}
