package com.marklogic.spring.batch.config;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.w3c.dom.Document;

import com.marklogic.spring.batch.data.Geoname;
import com.marklogic.spring.batch.data.GeonameFieldSetMapper;
import com.marklogic.spring.batch.processor.GeonamesItemProcessor;
import com.marklogic.spring.batch.writer.DocumentItemWriter;

@Configuration
@EnableBatchProcessing
public class Config {
	
   private Log log = LogFactory.getLog(Config.class);

   @Autowired
   private JobBuilderFactory jobs;

   @Autowired
   private StepBuilderFactory steps;
   
   @Bean
   public Job job1(@Qualifier("step1") Step step1) {
	   log.debug("JOB1");
	   return jobs.get("myJob").start(step1).build();
   }
   
   @Bean
   protected Step step1(ItemReader<Geoname> reader, ItemProcessor<Geoname, Document> processor, ItemWriter<Document> writer) {
	   log.debug("STEP1");
     return steps.get("step1")
    		 .<Geoname, Document> chunk(10)
    		 .reader(reader)
    		 .processor(processor)
    		 .writer(writer)
    		 .taskExecutor(taskExecutor())
    		 .build();
   }
   
   @Bean
   protected TaskExecutor taskExecutor() {
	   CustomizableThreadFactory tf = new CustomizableThreadFactory("geoname-threads");
	   SimpleAsyncTaskExecutor sate =  new SimpleAsyncTaskExecutor(tf);
	   sate.setConcurrencyLimit(10);
	   return sate;
   }
   
   @Bean
   protected ItemProcessor<Geoname, Document> processor() {
	   log.debug("ITEM PROCESSOR");
	   return new GeonamesItemProcessor();
   }
   
   @Bean
   protected ItemWriter<Document> writer() {
	   log.debug("ITEM WRITER");
	   return new DocumentItemWriter();
   }
   
   @Bean
   protected ItemReader<Geoname> reader() {
	   log.debug("ITEM READER");
	   try {
	   Resource res = new ClassPathResource("cities15000.txt");
	     File f = res.getFile();
	     f.exists();
	   } catch (Exception ex) { ex.printStackTrace(); }
     FlatFileItemReader<Geoname> reader = new FlatFileItemReader<Geoname>();
     reader.setResource(new ClassPathResource("cities15000.txt"));
     DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<Geoname>();
     mapper.setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB));
     mapper.setFieldSetMapper(new GeonameFieldSetMapper());
     reader.setLineMapper(mapper);
     return reader;
   }
}
