package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.client.eval.EvalResult;
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
	public Job corbJob() {
		log.info("corb");
		return jobBuilderFactory.get("corb").start(corb()).build();
	}
	   
	@Bean
	protected Step corb() {
		ItemProcessor<String, String> processor = new ItemProcessor<String, String>() {

			@Override
			public String process(String item) throws Exception {
				log.info(item);
				return item;
			}
			
		};	
		
	    return stepBuilderFactory.get("corb-step")
	    		.<String, String>chunk(1)
	    		.reader(new MarkLogicItemReader<String>(databaseClientProvider, "test.xqy"))
	    		.processor(processor)
	    		.writer(new MarkLogicItemWriter<String>(databaseClientProvider, "/process.xqy"))
	    		.build();
	} 

}
