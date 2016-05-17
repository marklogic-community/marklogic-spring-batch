package com.marklogic.spring.batch.job;

import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.junit.spring.BasicTestConfig.class,
        com.marklogic.spring.batch.job.IngestXmlFilesFromDirectory.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
})
public class IngestXmlFilesFromDirectoryTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    }

}
