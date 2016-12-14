package com.marklogic.spring.batch.samples;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.spring.batch.samples.YourJob.class,
        com.marklogic.spring.batch.test.JobRunnerContext.class,
        com.marklogic.spring.batch.test.JobProjectTestConfig.class})
public class YourJobTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    DatabaseClientProvider databaseClientProvider;

    private ClientTestHelper client;
    
    @Before
    public void setup() {
        client = new ClientTestHelper();
        client.setDatabaseClientProvider(databaseClientProvider);
    }

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void findZeroMonstersInDatabaseTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        /*
        runJob(
                YourJobConfig.class,
                "--output_collections", "monster");
             */
        client.assertCollectionSize("Expecting 1 items in monster collection", "monster", 1);
    }

}
