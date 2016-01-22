package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

import com.marklogic.client.spring.batch.geonames.GeonamesConfig;

public class CorbConfig {
	
	private Log log = LogFactory.getLog(CorbConfig.class);

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	   
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;
	
	@Bean
	public Tasklet getUrisTasklet() {
		return new GetUrisTasklet();
	}
	
	@Bean
	public Job corb(@Qualifier("getUrisTasklet") Step step1) {
		log.debug("corb");
		return jobs.get("corb").start(step1).build();
	}
	   
	@Bean
	protected Step getUrisTasklet(Tasklet getUrisTasklet) {
		log.debug("Run Corb");
	    return steps.get("corb-uris")
	    		.tasklet(getUrisTasklet)
	    		.build();
	}   

}
