package com.marklogic.spring.batch.core.explore;

import java.util.List;

import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
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
	
	@Test
	public void ReturnJobExecutionsAreDescendingOrderTest() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobInstance jobInstance = new JobInstance(123L, "firstJob");
		JobExecution jobExecution1 = jobRepository.createJobExecution(jobInstance, JobParametersTestUtils.getJobParameters(), null);
		jobExecution1.setStatus(BatchStatus.FAILED);
		
		JobParametersBuilder builder = new JobParametersBuilder();
		builder.addLong("test", 12345L, true);
		JobExecution jobExecution2 = jobRepository.createJobExecution(jobInstance, builder.toJobParameters(), null);
		
		builder.addLong("test2", 12346L, true);
		JobExecution jobExecution3 = jobRepository.createJobExecution(jobInstance, builder.toJobParameters(), null);
		
		List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
		assertNotNull(jobExecutions);
		assertEquals(jobExecution3.getId(), jobExecutions.get(0).getId());
		assertEquals(jobExecution2.getId(), jobExecutions.get(1).getId());
		assertEquals(jobExecution1.getId(), jobExecutions.get(2).getId());	
	}
		
}
