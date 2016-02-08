package com.marklogic.spring.batch.core.repository;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.repository.MarkLogicSpringBatchRepository;

public class PersistSimpleJobInstanceTest extends AbstractSpringBatchTest {
	
	private JobExecution jobExecution;
	
	@Autowired
	Environment environment;
	
	@Before
	public void setup() {
		ConfigurableEnvironment environment = new MockEnvironment();
		environment.setActiveProfiles("marklogic");
	}
	
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
        f.assertElementExists(format("/sb:jobExecution/sb:jobInstance/sb:id[text() = %s]", id));		
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
