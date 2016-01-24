package com.marklogic.client.spring.batch.corb;

import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.client.spring.batch.AbstractSpringBatchTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.corb.CorbConfig.class } )
public class CorbJobTest extends AbstractSpringBatchTest {
	
	@Autowired
	Job corbJob;
	
	@Test
	public void corbTest() throws Exception {
		jobLauncherTestUtils.setJob(corbJob);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
