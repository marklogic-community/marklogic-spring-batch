package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.client.helper.DatabaseClientProvider;

@Configuration
public class CorbConfig {
	
	private Log log = LogFactory.getLog(CorbConfig.class);

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;
	
	@Bean
	public Job corb() {
		log.info("corb");
		return jobBuilderFactory.get("corb")
				.start(getUrisStep())
				.next(processDocumentStep())
				.build();
	}
	   
	@Bean
	protected Step getUrisStep() {
		log.info("Get URIS");
	    return stepBuilderFactory.get("corb-uris")
	    		.tasklet(new GetUrisTasklet(databaseClientProvider))
	    		.build();
	} 
	
	@Bean
	protected Step processDocumentStep() {
		log.info("Process Document");
	    return stepBuilderFactory.get("corb-process")
	    		.tasklet(new ProcessDocumentTasklet(databaseClientProvider))
	    		.build();
	}

}
