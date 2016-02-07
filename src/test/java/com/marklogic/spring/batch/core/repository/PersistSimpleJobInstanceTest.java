package com.marklogic.spring.batch.core.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobRepositoryTestUtils;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.repository.MarkLogicSpringBatchRepository;

public class PersistSimpleJobInstanceTest extends AbstractSpringBatchTest {
	
	private JobExecution jobExecution;
	
	@Test
	public void persistSimpleJobInstanceTest() throws Exception {
		givenAJobExecution();
		whenJobIsExecuted();
		thenVerifyJobInstanceIsPersisted();
	}

    private void thenVerifyJobInstanceIsPersisted() {
    	XMLDocumentManager xmlDocMgr = getClient().newXMLDocumentManager();
        String id = jobExecution.getId().toString();
        StringHandle handle = xmlDocMgr.read(MarkLogicSpringBatchRepository.SPRING_BATCH_DIR + "/job-instance/" + id,
                new StringHandle());
        Fragment f = parse(handle.toString());
        f.assertElementExists(format("/sb:job-instance/sb:job-execution/sb:id[text() = %s]", id));		
	}

	private void whenJobIsExecuted() {
		
	}

	private void givenAJobExecution() throws Exception {
		JobRepositoryTestUtils jobRepositoryTestUtils = newJobRepositoryTestUtils();
        List<JobExecution> jobExecutions = jobRepositoryTestUtils.createJobExecutions(1);
        assertFalse(jobExecutions.isEmpty());
        jobExecution = jobExecutions.get(0);		
	}

}
