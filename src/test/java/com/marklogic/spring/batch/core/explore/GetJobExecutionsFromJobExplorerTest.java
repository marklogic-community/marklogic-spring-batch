package com.marklogic.spring.batch.core.explore;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;


import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobExecutionTestUtils;
import com.marklogic.spring.batch.core.AdaptedJobExecution;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class GetJobExecutionsFromJobExplorerTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JAXBContext jaxbContext;
	
	@Autowired
	private JobExplorer jobExplorer;
	
	private JobInstance jobInstance;
	private JobExecution jobExec;
	private List<JobExecution> jobExecutions;
	private Long jobExecId;
	private XMLDocumentManager docMgr;
	
	@Before
	public void setup() throws Exception {
		DatabaseClient client = getClient();
		docMgr = client.newXMLDocumentManager();
		DatabaseClientFactory.getHandleRegistry().register(JAXBHandle.newFactory(AdaptedJobExecution.class));
	}
	
	@Test
	public void getJobExecutionByJobInstanceTest() throws Exception {
		givenAJobExecution();
		whenGetJobExecutionByJobInstanceFromJobExplorer();
		assertEquals(1, jobExecutions.size());
		thenVerifyJobExecution();
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
	
	private void whenGetJobExecutionByJobInstanceFromJobExplorer() {
		jobExecutions = jobExplorer.getJobExecutions(jobInstance);
		jobExec = jobExecutions.get(0);
	}

	private void whenGetJobExecutionFromJobExplorer() {
		jobExec = jobExplorer.getJobExecution(jobExecId);
	}

	private void givenAJobExecution() throws JAXBException {		
		JobExecution tempJob = JobExecutionTestUtils.getJobExecution();
		jobInstance = tempJob.getJobInstance();
		jobExecId = tempJob.getId();
		AdaptedJobExecution jobExecution = new AdaptedJobExecution(JobExecutionTestUtils.getJobExecution());
		JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
		handle.set(jobExecution);
		docMgr.write(jobExecution.getUri(), handle);
	}

		
}
