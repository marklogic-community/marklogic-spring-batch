package com.marklogic.spring.batch.samples;

import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Assert;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { YourJobConfig.class })
public class YourJobTest extends AbstractJobRunnerTest {

    @Test
    public void findOneMonsterInDatabaseTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting 1 items in monster collection", "monster", 1);
        getClientTestHelper().assertCollectionSize("Expecting one job instance in JobRepo", "http://marklogic.com/spring-batch/job-instance", 1);
    }

    @Test
    public void runJobTwiceTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster", false);
        jpb.addLong("run_id", 1L, true);
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        jpb.addLong("run_id", 2L);
        JobExecution jobExecution2 = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution2.getStatus());
    }


}
