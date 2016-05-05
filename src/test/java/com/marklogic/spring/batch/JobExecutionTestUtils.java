package com.marklogic.spring.batch;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ExecutionContext;

public class JobExecutionTestUtils {
	
	public static JobExecution getJobExecution() {
		JobInstance jobInstance = new JobInstance(Math.abs(new Random(100L).nextLong()), "sampleJob");
		JobExecution jobExecution = new JobExecution(jobInstance, Long.valueOf(123L), JobParametersTestUtils.getJobParameters(), "abc");
		jobExecution.createStepExecution("sampleStep1");
		jobExecution.createStepExecution("sampleStep2");
		ExecutionContext ec = new ExecutionContext();
		ec.put("testName", "testValue");
		jobExecution.setExecutionContext(ec);
		return jobExecution;
	}
}
