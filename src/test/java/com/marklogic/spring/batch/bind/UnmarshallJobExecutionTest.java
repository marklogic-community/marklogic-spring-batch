package com.marklogic.spring.batch.bind;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.AdaptedJobParameters;

public class UnmarshallJobExecutionTest extends AbstractSpringBatchTest {
	
	Unmarshaller unmarshaller;
	
	@Before
	public void setup() throws Exception {
		unmarshaller = JAXBContext.newInstance(AdaptedJobParameters.class).createUnmarshaller();
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
		assertEquals(params.getParameters().get(0).value, "Joe Cool");
		assertEquals(params.getParameters().get(1).value, "2016-02-15T21:39:21-0500");
		assertEquals(params.getParameters().get(2).value, "1239");
		assertEquals(params.getParameters().get(3).value, "1.35");
	}

}
