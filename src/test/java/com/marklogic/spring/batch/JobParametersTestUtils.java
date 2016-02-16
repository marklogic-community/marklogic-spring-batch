package com.marklogic.spring.batch;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

public class JobParametersTestUtils {
	
	public JobParameters getJobParameters() {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("stringTest", "Joe Cool", true);
		jobParametersBuilder.addDate("start", new Date(), false);
		jobParametersBuilder.addLong("longTest", 1239L, false);
		jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
		return jobParametersBuilder.toJobParameters();
		
	}
}
