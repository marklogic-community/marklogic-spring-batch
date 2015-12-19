package com.marklogic.spring.batch.config;

import java.io.File;
import java.util.List;

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

import com.marklogic.spring.batch.data.Geoname;
import com.marklogic.spring.batch.data.GeonameFieldSetMapper;

@Configuration
@EnableBatchProcessing
public class Config {
	
   @Autowired
   private JobBuilderFactory jobs;

   @Autowired
   private StepBuilderFactory steps;
   
   @Bean
   public Job job1(@Qualifier("step1") Step step1) {
	   System.out.println("JOB1");
	   return jobs.get("myJob").start(step1).build();
   }
   
   @Bean
   protected Step step1(ItemReader<Geoname> reader, ItemProcessor<Geoname, Geoname> processor, ItemWriter<Geoname> writer) {
	   System.out.println("STEP1");
     return steps.get("step1")
    		 .<Geoname, Geoname> chunk(10)
    		 .reader(reader)
    		 .processor(processor)
    		 .writer(writer)
    		 .build();
   }
   
   @Bean
   protected ItemProcessor<Geoname, Geoname> processor() {
	   System.out.println("ITEM PROCESSOR");
	   return new GeonameItemProcessor();
   }
   
   @Bean
   protected ItemWriter<Geoname> writer() {
	   System.out.println("ITEM WRITER");
	   return new GeonameItemWriter();
   }
   
   @Bean
   protected ItemReader<Geoname> reader() {
	   System.out.println("ITEM READER");
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
  
   public class GeonameItemProcessor implements ItemProcessor<Geoname, Geoname> {

	   @Override
	   public Geoname process(Geoname item) throws Exception {
		   // TODO Auto-generated method stub
		   //System.out.println("PROCESS: " + item.getId());
		   return item;
	   }
   }

   public class GeonameItemWriter implements ItemWriter<Geoname> {

	@Override
	public void write(List<? extends Geoname> items) throws Exception {
		System.out.println("WRITE");
		for (Geoname item : items) {
			System.out.println(item.getId() + item.getName());
		}
		
	}

	   
   }
}
