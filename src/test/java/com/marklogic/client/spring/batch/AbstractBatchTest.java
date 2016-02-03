package com.marklogic.client.spring.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.junit.spring.AbstractSpringTest;

@ContextConfiguration(classes = { TestConfig.class })
public abstract class AbstractBatchTest extends AbstractSpringTest {

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    /**
     * Convenience method for creating a JobLauncherTestUtils that can be used to launch a job or a step in a test.
     * 
     * @return
     */
    protected JobLauncherTestUtils newJobLauncherTestUtils() {
        JobLauncherTestUtils utils = new JobLauncherTestUtils();
        utils.setJobLauncher(jobLauncher);
        return utils;
    }

    /**
     * Convenience method for testing a single step; the subclass doesn't have to bother with creating a job.
     * 
     * @param step
     * @return
     */
    protected void launchJobWithStep(Step step) {
        Job job = jobBuilderFactory.get("testJob").start(step).build();
        JobLauncherTestUtils utils = newJobLauncherTestUtils();
        utils.setJob(job);

        JobExecution jobExecution = null;
        try {
            jobExecution = utils.launchJob();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        logger.info(
                "Job execution time: " + (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()));
    }
}
