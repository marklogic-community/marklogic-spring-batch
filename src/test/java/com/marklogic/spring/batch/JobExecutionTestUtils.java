package com.marklogic.spring.batch;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

public class JobExecutionTestUtils {
	
	public static JobExecution getJobExecution() {
		JobExecution jobExecution = new JobExecution(new JobInstance(Math.abs(new Random(100L).nextLong()), "sampleJob"), JobParametersTestUtils.getJobParameters());
		jobExecution.setId(Math.abs(new Random(100L).nextLong()));
		jobExecution.createStepExecution("sampleStep1");
		jobExecution.createStepExecution("sampleStep2");
		return jobExecution;
	}
}
