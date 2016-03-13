package com.marklogic.spring.batch;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class JobParametersTestUtils implements JobParametersIncrementer {
	
	private static JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
	
	public static JobParameters getJobParameters() {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		if (jobParameters.isEmpty()) {
			jobParametersBuilder.addLong("id", 1L, true);
			jobParametersBuilder.addString("stringTest", "Joe Cool", true);
			jobParametersBuilder.addDate("start", new Date(), false);
			jobParametersBuilder.addLong("longTest", 1239L, false);
			jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
			jobParameters = jobParametersBuilder.toJobParameters();
		} else {
			JobParametersTestUtils utils = new JobParametersTestUtils();
			jobParameters = utils.getNext(jobParameters);
		}
		return jobParameters;
		
	}

	@Override
	public JobParameters getNext(JobParameters parameters) {
		JobParametersBuilder builder = new JobParametersBuilder(parameters);
		builder.addLong("id", parameters.getLong("id") + 1);
		return builder.toJobParameters();
	}
}
