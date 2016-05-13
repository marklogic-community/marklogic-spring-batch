package com.marklogic.spring.batch;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.SpringBatchNamespaceProvider;
import com.marklogic.spring.batch.core.AdaptedExecutionContext;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicJobInstance;

@ActiveProfiles("default")
@ContextConfiguration(classes = { 
		com.marklogic.junit.spring.BasicTestConfig.class, 
		com.marklogic.spring.batch.test.MarkLogicSpringBatchTestConfig.class,
		com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class,
		com.marklogic.spring.batch.configuration.DefaultBatchConfiguration.class })
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {

    @Autowired
    protected JobLauncher jobLauncher;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

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
    
    protected JobParametersTestUtils newJobParametersUtils() {
    	return new JobParametersTestUtils();
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

        JobExecution jobExecution;
        try {
            jobExecution = utils.launchJob();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        logger.info(
                "Job execution time: " + (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime()));
    }
    
    protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class, AdaptedJobInstance.class, 
            		AdaptedJobParameters.class, AdaptedStepExecution.class, AdaptedExecutionContext.class, MarkLogicJobInstance.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}
}
