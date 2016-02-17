package com.marklogic.spring.batch.core.explore;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
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
		
	}

	private void whenGetJobExecutionFromJobExplorer() {
		jobExec = jobExplorer.getJobExecution(12345L);
	}

	private void givenAJobExecution() throws JAXBException {		
		JobInstance jobInstance = new JobInstance(123L, "TestJobInstance");
		JobExecution jobExec1 = new JobExecution(jobInstance, newJobParametersUtils().getJobParameters());
		jobExec1.setId(12345L);
		List<StepExecution> listOfSteps = new ArrayList<StepExecution>();
		listOfSteps.add(new StepExecution("Step1", jobExec1));
		AdaptedJobExecution jobExecution = new AdaptedJobExecution(jobExec1);
		JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
		handle.set(jobExecution);
		docMgr.write(jobExecution.getUri(), handle);
	}

		
}
