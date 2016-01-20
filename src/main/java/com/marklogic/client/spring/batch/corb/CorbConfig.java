package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CorbConfig {
	
	private Log log = LogFactory.getLog(CorbConfig.class);

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;
	
	@Bean
	public Tasklet getUrisTasklet() {
		return new GetUrisTasklet();
	}
	
	@Bean
	public Job job1(@Qualifier("step1") Step step1) {
		log.debug("corb");
		return jobs.get("corb").start(step1).build();
	}
	   
	@Bean
	protected Step step1(Tasklet getUrisTasklet) {
		log.debug("Run Corb");
	    return steps.get("step1")
	    		.tasklet(getUrisTasklet)
	    		.build();
	}   

}
