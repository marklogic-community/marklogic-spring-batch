package com.marklogic.spring.batch.job;

import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.junit.spring.BasicTestConfig.class,
        com.marklogic.spring.batch.job.LoadDocumentsFromDirectoryJob.class,
        com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class
})
public class LoadDocumentsFromDirectoryJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    }

}
