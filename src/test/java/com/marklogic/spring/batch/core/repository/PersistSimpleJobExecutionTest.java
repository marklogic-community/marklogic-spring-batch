package com.marklogic.spring.batch.core.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class PersistSimpleJobExecutionTest extends AbstractSpringBatchTest {
	
	private JobExecution jobExecution;
	
	@Test
	public void persistSimpleJobExecutionTest() throws Exception {
		givenAJobExecution();
		whenJobIsExecuted();
		thenVerifyJobInstanceIsPersisted();
	}

    private void thenVerifyJobInstanceIsPersisted() {
    	XMLDocumentManager xmlDocMgr = getClient().newXMLDocumentManager();
        String id = jobExecution.getId().toString();
        StringHandle handle = xmlDocMgr.read(MarkLogicSpringBatch.SPRING_BATCH_DIR + id + ".xml",
                new StringHandle());
        Fragment f = parse(handle.toString());
        f.prettyPrint();
        f.assertElementValue("/msb:jobExecution/msb:id", id);	
        f.assertElementExists("//msb:stepExecutions");
	}

	private void whenJobIsExecuted() {
		
	}

	private void givenAJobExecution() throws Exception {
        List<JobExecution> jobExecutions = newJobRepositoryTestUtils().createJobExecutions(1);
        assertFalse(jobExecutions.isEmpty());
        jobExecution = jobExecutions.get(0);	
        jobExecution.createStepExecution("ABC-Step");
	}

}
