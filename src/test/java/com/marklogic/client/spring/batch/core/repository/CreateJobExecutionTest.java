package com.marklogic.client.spring.batch.core.repository;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.spring.batch.AbstractSpringBatchTest;
import com.marklogic.junit.Fragment;

public class CreateJobExecutionTest extends AbstractSpringBatchTest {
	
	@Before
	public void setJobRepository() {
		jobRepositoryTestUtils.setJobRepository(jobRepository);
	}
	
	@Test
	public void createSingleJobExecutionTest() throws Exception {
		List<JobExecution> jobExecutions = jobRepositoryTestUtils.createJobExecutions(1);
		assertFalse(jobExecutions.isEmpty());
		XMLDocumentManager xmlDocMgr = databaseClientProvider.getDatabaseClient().newXMLDocumentManager();
		String id = jobExecutions.get(0).getId().toString();
		StringHandle handle = xmlDocMgr.read(MarkLogicSpringBatchRepository.SPRING_BATCH_DIR + "/job-execution/" + id, new StringHandle());
		Fragment f = new Fragment(handle.toString(), nsProvider.getNamespaces());
		f.assertElementExists(format("/sb:jobExecution/sb:id[text() = %s]", id));
		
	}

}
