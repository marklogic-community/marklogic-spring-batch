package com.marklogic.spring.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

public class JobExecutionTestUtils {
	
	public static JobExecution getJobExecution() {
		JobExecution jobExecution = new JobExecution(new JobInstance(123L, "sampleJob"), JobParametersTestUtils.getJobParameters());
		jobExecution.setId(12345L);
		jobExecution.createStepExecution("sampleStep1");
		jobExecution.createStepExecution("sampleStep2");
		return jobExecution;
	}
}
