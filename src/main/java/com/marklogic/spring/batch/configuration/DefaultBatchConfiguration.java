package com.marklogic.spring.batch.configuration;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile("default")
@EnableBatchProcessing(modular=true)
public class DefaultBatchConfiguration {
	
	@Bean
	public JobParametersBuilder jobParametersBuilder() {
		return new JobParametersBuilder();
	}

}
