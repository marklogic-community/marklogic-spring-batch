package com.marklogic.spring.batch.bind;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class UnmarshallSpringBatchPojoTest extends AbstractSpringBatchTest {
	
	Unmarshaller unmarshaller;
	
	@Before
	public void setup() throws Exception {
		unmarshaller = JAXBContext.newInstance(AdaptedJobParameters.class, AdaptedJobInstance.class, AdaptedJobExecution.class).createUnmarshaller();
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
	
	@Test
	public void unmarshallJobExecution() throws Exception {
		StringReader xml = new StringReader("<msb:jobExecution xmlns:msb=\"http://projects.spring.io/spring-batch\">" +
				"<msb:id>12345</msb:id>" +
			    "<msb:createDateTime>2016-02-16T09:37:14.263-05:00</msb:createDateTime>" +
			    "<msb:exitCode>exitCode=UNKNOWN;exitDescription=</msb:exitCode>" +
			    "<msb:jobInstance>" +
			        "<msb:id>123</msb:id>" +
			        "<msb:jobName>TestJobInstance</msb:jobName>" +
			    "</msb:jobInstance>" +
			    "<msb:jobParameters>" +
			        "<msb:jobParameter key=\"stringTest\" type=\"STRING\" identifier=\"true\">Joe Cool</msb:jobParameter>" +
			        "<msb:jobParameter key=\"start\" type=\"DATE\" identifier=\"false\">2016-02-16T09:37:14-0500</msb:jobParameter>" +
			        "<msb:jobParameter key=\"longTest\" type=\"LONG\" identifier=\"false\">1239</msb:jobParameter>" +
			        "<msb:jobParameter key=\"doubleTest\" type=\"DOUBLE\" identifier=\"false\">1.35</msb:jobParameter>" +
			    "</msb:jobParameters>" +
			    "<msb:status>STARTING</msb:status>" +
			    "<msb:stepExecutions/>" +
			"</msb:jobExecution>");
		AdaptedJobExecution jobExecution = (AdaptedJobExecution)unmarshaller.unmarshal(xml);
		assertEquals(MarkLogicSpringBatch.SPRING_BATCH_DIR + "12345", jobExecution.getUri());
		assertEquals(Long.valueOf(12345L), jobExecution.getId());
		
		JobExecutionAdapter adapter = new JobExecutionAdapter();
		JobExecution jobExec = adapter.unmarshal(jobExecution);
		assertNotNull(jobExec);
		assertEquals(Long.valueOf(12345L), jobExecution.getId());
	}
	
	@Test
	public void unmarshallStepExecution() throws Exception {
		StringReader xml = new StringReader("<msb:stepExecution xmlns:msb=\"http://projects.spring.io/spring-batch\">" +
			      "<msb:commitCount>0</msb:commitCount>" +
			      "<msb:exitStatus />" +
			      "<msb:filterCount>0</msb:filterCount>" +
			      "<msb:processSkipCount>0</msb:processSkipCount>" +
			      "<msb:readCount>0</msb:readCount>" +
			      "<msb:readSkipCount>0</msb:readSkipCount>" +
			      "<msb:rollbackCount>0</msb:rollbackCount>" +
			      "<msb:startTime>2016-02-17T16:48:24.927-05:00</msb:startTime>" +
			      "<msb:status>STARTING</msb:status>" +
			      "<msb:stepName>sampleStep1</msb:stepName>" +
			      "<msb:terminateOnly>false</msb:terminateOnly>" +
			      "<msb:writeCount>0</msb:writeCount>" +
			      "<msb:writeSkipCount>0</msb:writeSkipCount>" +
			    "</msb:stepExecution>");
		AdaptedStepExecution stepExecution = (AdaptedStepExecution)unmarshaller.unmarshal(xml);
		assertEquals("sampleStep1", stepExecution.getStepName());
	}

}
