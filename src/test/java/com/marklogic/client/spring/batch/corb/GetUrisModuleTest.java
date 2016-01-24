package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.client.spring.batch.AbstractSpringBatchTest;

@ContextConfiguration(classes = { com.marklogic.client.spring.batch.corb.CorbConfig.class } )
public class GetUrisModuleTest extends AbstractSpringBatchTest {
	
	@Autowired
	Job corbJob;
	
	private Log log = LogFactory.getLog(this.getClass());	
	
	@Test
	public void corbTest() throws Exception {
		jobLauncherTestUtils.setJob(corbJob);
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
