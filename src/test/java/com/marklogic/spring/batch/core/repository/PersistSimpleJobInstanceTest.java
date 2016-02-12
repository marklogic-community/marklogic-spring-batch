package com.marklogic.spring.batch.core.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.repository.MarkLogicSpringBatchRepository;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
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
        StringHandle handle = xmlDocMgr.read(MarkLogicSpringBatchRepository.SPRING_BATCH_DIR + "/job-execution/" + id,
                new StringHandle());
        Fragment f = parse(handle.toString());
        f.assertElementExists(format("/sb:jobInstance/sb:id[text() = %s]", id));		
	}

	private void whenJobIsExecuted() {
		
	}

	private void givenAJobExecution() throws Exception {
        List<JobExecution> jobExecutions = newJobRepositoryTestUtils().createJobExecutions(1);
        assertFalse(jobExecutions.isEmpty());
        jobExecution = jobExecutions.get(0);		
	}

}
