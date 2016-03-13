package com.marklogic.spring.batch.core.explore;

import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
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
	
	/*
	 * Given a JobInstance
	 * When the job instance is retried by ID
	 * Then 
	 */
	@Test
	public void retrieveJobInstanceById() {
		JobInstance expectedJobInstance = jobRepository.createJobInstance(JOB_NAME, JobParametersTestUtils.getJobParameters());
		JobInstance actualJobInstance = jobExplorer.getJobInstance(expectedJobInstance.getId());
		assertTrue(expectedJobInstance.equals(actualJobInstance));
	}
	
	

}
