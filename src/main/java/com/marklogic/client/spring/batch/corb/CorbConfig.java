package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
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
	public Job corbJob() {
		log.info("corb");
		return jobBuilderFactory.get("corbJob").start(corb()).build();
	}
	   
	protected Step corb() {
		
		ItemProcessor<String, String> processor = new ItemProcessor<String, String>() {
			@Override
			public String process(String item) throws Exception {
				log.info(item);
				return item;
			}
		};	
		
	    return stepBuilderFactory.get("corbStep")
	    		.<String, String>chunk(10)
	    		.reader(uriReader())
	    		.processor(processor)
	    		.writer(processWriter())
	    		.build();
	} 

	public ItemReader<String> uriReader() {
		MarkLogicItemReader<String> reader = new MarkLogicItemReader<String>(databaseClientProvider, "/ext/corb/uris.xqy");
		return reader;
	}
	
	public ItemWriter<String> processWriter() {
		MarkLogicItemWriter<String> writer = new MarkLogicItemWriter<String>(databaseClientProvider, "/ext/corb/process.xqy");
		return writer;
	}

}
