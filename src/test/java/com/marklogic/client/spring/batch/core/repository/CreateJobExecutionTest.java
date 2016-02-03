package com.marklogic.client.spring.batch.core.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobRepositoryTestUtils;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.spring.batch.AbstractSpringBatchTest;
import com.marklogic.junit.Fragment;

public class CreateJobExecutionTest extends AbstractSpringBatchTest {

    @Test
    public void createSingleJobExecutionTest() throws Exception {
        JobRepositoryTestUtils jobRepositoryTestUtils = newJobRepositoryTestUtils();
        List<JobExecution> jobExecutions = jobRepositoryTestUtils.createJobExecutions(1);
        assertFalse(jobExecutions.isEmpty());
        XMLDocumentManager xmlDocMgr = getClient().newXMLDocumentManager();
        String id = jobExecutions.get(0).getId().toString();
        StringHandle handle = xmlDocMgr.read(MarkLogicSpringBatchRepository.SPRING_BATCH_DIR + "/job-execution/" + id,
                new StringHandle());
        Fragment f = parse(handle.toString());
        f.assertElementExists(format("/sb:jobExecution/sb:id[text() = %s]", id));

    }

}
