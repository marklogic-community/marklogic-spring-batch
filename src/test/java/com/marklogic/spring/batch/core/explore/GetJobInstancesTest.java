package com.marklogic.spring.batch.core.explore;

import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobParametersTestUtils;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class GetJobInstancesTest extends AbstractSpringBatchTest {
	
	@Autowired
	JobExplorer jobExplorer;
	
	@Autowired
	JobRepository jobRepository;
	
	private final String JOB_NAME = "testJob";
	
	@Test
	public void retrieveJobInstanceByIdTest() {
		JobInstance expectedJobInstance = jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		JobInstance actualJobInstance = jobExplorer.getJobInstance(expectedJobInstance.getId());
		assertTrue(expectedJobInstance.equals(actualJobInstance));
	}
	
	@Test
	public void getJobInstanceCountTest() throws NoSuchJobException {
		jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		assertEquals(2, jobExplorer.getJobInstanceCount(JOB_NAME));
	}
	
	@Test(expected=NoSuchJobException.class)
	public void getJobInstanceCountNoJobException() throws NoSuchJobException {
		jobExplorer.getJobInstanceCount("NoJobs");
	}

}
