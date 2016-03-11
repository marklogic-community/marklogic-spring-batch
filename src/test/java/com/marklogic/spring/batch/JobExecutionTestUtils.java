package com.marklogic.spring.batch;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

public class JobExecutionTestUtils {
	
	public static JobExecution getJobExecution() {
		JobInstance jobInstance = new JobInstance(Math.abs(new Random(100L).nextLong()), "sampleJob");
		JobExecution jobExecution = new JobExecution(jobInstance, Long.valueOf(123L), JobParametersTestUtils.getJobParameters(), "abc");
		jobExecution.createStepExecution("sampleStep1");
		jobExecution.createStepExecution("sampleStep2");
		return jobExecution;
	}
}
