package com.marklogic.spring.batch.core.explore;

import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.DatabaseClient;
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
	private JobParametersBuilder jobParametersBuilder;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	private JobExecution jobExec;
	private Long jobExecId;
	
	private XMLDocumentManager docMgr;
	
	@Before
	public void setup() throws Exception {
		DatabaseClient client = databaseClientProvider.getDatabaseClient();
		docMgr = client.newXMLDocumentManager();
	}

	
	@Test
	public void FindJobInstanceByJobNameTest() throws Exception {
		givenAJobExecution();
		whenGetJobExecutionFromJobExplorer();
		thenVerifyJobExecution();
	}

	private void thenVerifyJobExecution() {
		assertEquals(jobExec.getJobParameters().getString("stringTest"), "Joe Cool");
		
	}

	private void whenGetJobExecutionFromJobExplorer() {
		jobExec = jobExplorer.getJobExecution(jobExecId);
	}

	private void givenAJobExecution() throws JAXBException {
		jobParametersBuilder.addString("stringTest", "Joe Cool", true);
		jobParametersBuilder.addDate("start", new Date(), false);
		jobParametersBuilder.addLong("longTest", 1239L, false);
		jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
		
		JobInstance jobInstance = new JobInstance(123L, "TestJobInstance");
		
		JobExecution jobExec1 = new JobExecution(jobInstance, jobParametersBuilder.toJobParameters());
		jobExec1.setId(12345L);
		AdaptedJobExecution jobExecution = new AdaptedJobExecution(jobExec1);
		JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
		handle.set(jobExecution);
		docMgr.write(jobExecution.getId(), handle);
	}

		
}
