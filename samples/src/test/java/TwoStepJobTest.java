package com.marklogic.spring.batch.samples;

import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { YourTwoStepJobConfig.class })
public class TwoStepJobTest extends AbstractJobRunnerTest {

    @Test
    public void runJobTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
