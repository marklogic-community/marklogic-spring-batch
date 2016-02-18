package com.marklogic.spring.batch.core.explore;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobExecutionTestUtils;
import com.marklogic.spring.batch.core.AdaptedJobExecution;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class GetJobExecutionsFromJobExplorerTest extends AbstractSpringBatchTest {

	@Autowired
	private DatabaseClientProvider databaseClientProvider;
	
	@Autowired
	private JAXBContext jaxbContext;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	private JobExecution jobExec;
	
	private XMLDocumentManager docMgr;
	
	@Before
	public void setup() throws Exception {
		DatabaseClient client = databaseClientProvider.getDatabaseClient();
		docMgr = client.newXMLDocumentManager();
		DatabaseClientFactory.getHandleRegistry().register(JAXBHandle.newFactory(AdaptedJobExecution.class));
	}

	
	@Test
	public void getJobExecutionByIdTest() throws Exception {
		givenAJobExecution();
		whenGetJobExecutionFromJobExplorer();
		thenVerifyJobExecution();
	}

	private void thenVerifyJobExecution() {
		assertEquals("Joe Cool", jobExec.getJobParameters().getString("stringTest"));
		assertEquals("sampleJob", jobExec.getJobInstance().getJobName());
		assertFalse(jobExec.getStepExecutions().isEmpty());
		assertEquals(2, jobExec.getStepExecutions().size());		
	}

	private void whenGetJobExecutionFromJobExplorer() {
		jobExec = jobExplorer.getJobExecution(12345L);
	}

	private void givenAJobExecution() throws JAXBException {		
		AdaptedJobExecution jobExecution = new AdaptedJobExecution(JobExecutionTestUtils.getJobExecution());
		JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
		handle.set(jobExecution);
		docMgr.write(jobExecution.getUri(), handle);
	}

		
}
