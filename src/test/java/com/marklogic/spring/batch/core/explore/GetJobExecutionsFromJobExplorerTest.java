package com.marklogic.spring.batch.core.explore;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobParametersTestUtils;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class GetJobExecutionsFromJobExplorerTest extends AbstractSpringBatchTest {

	
	@Autowired
	private JobExplorer jobExplorer;
	
	@Autowired
	private JobRepository jobRepository;

	@Test
	public void getJobExecutionByJobInstanceTest() throws Exception {
		JobInstance jobInstance = jobRepository.createJobInstance("sampleJob", JobParametersTestUtils.getJobParameters());
		List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
		assertEquals(1, jobExecutions.size());
		assertEquals(jobExecutions.get(0).getJobInstance().getInstanceId(), jobInstance.getInstanceId());
	}

	@Test
	public void getJobExecutionByIdTest() throws Exception {
		JobExecution jobExecution = jobRepository.createJobExecution("sampleJob", JobParametersTestUtils.getJobParameters());
		JobExecution persistedJobExecution = jobExplorer.getJobExecution(jobExecution.getId());
		assertEquals(persistedJobExecution.getId(), jobExecution.getId());
	}
		
}
