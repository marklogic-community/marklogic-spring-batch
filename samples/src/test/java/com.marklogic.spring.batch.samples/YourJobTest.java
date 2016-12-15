package com.marklogic.spring.batch.samples;

import com.marklogic.spring.batch.test.AbstractJobTest;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.YourJob.class})
public class YourJobTest extends AbstractJobTest {

    @Test
    public void findOneMonsterInDatabaseTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        getClientTestHelper().assertCollectionSize("Expecting 1 items in monster collection", "monster", 1);
    }

}
