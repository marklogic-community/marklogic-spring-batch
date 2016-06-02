package com.marklogic.spring.batch.geonames;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.core.task.TaskExecutor;
import org.w3c.dom.Document;

import com.marklogic.spring.batch.item.DocumentItemWriter;

import org.geonames.Geoname;

@Configuration
public class GeonamesConfig {
	
   private Log log = LogFactory.getLog(GeonamesConfig.class);

   @Autowired
   private JobBuilderFactory jobBuilderFactory;

   @Autowired
   private StepBuilderFactory stepBuilderFactory;
   
   @Autowired
   private TaskExecutor taskExecutor;
     
   @Bean
   public Job loadGeonamesJob(@Qualifier("loadGeonamesStep") Step step1) {
	   log.info("Geonames Job");
	   return jobBuilderFactory.get("loadGeonamesJob").start(step1).build();
   }
   
   @Bean
   protected Step loadGeonamesStep(ItemReader<Geoname> reader, ItemProcessor<Geoname, Document> processor, ItemWriter<Document> writer) {
	 log.info("Import Geonames");
     return stepBuilderFactory.get("step1")
    		 .<Geoname, Document> chunk(10)
    		 .reader(reader)
    		 .processor(processor)
    		 .writer(writer)
    		 .taskExecutor(taskExecutor)
    		 .build();
   }   
   
   @Bean
   protected ItemProcessor<Geoname, Document> processor() {
	   log.info("ITEM PROCESSOR");
	   return new GeonamesItemProcessor();
   }
   
   @Bean
   protected ItemWriter<Document> writer() {
	   log.info("ITEM WRITER");
	   return new DocumentItemWriter();
   }
   
   @Bean
   protected ItemReader<Geoname> geonameReader() {
	   log.info("GEONAMES ITEM READER");
	   FlatFileItemReader<Geoname> reader = new FlatFileItemReader<>();
	   reader.setResource(new ClassPathResource("geonames/cities15000.txt"));
	   DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<>();
	   DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
	   tokenizer.setQuoteCharacter('{');
	   mapper.setLineTokenizer(tokenizer);
	   mapper.setFieldSetMapper(new GeonameFieldSetMapper());
	   reader.setLineMapper(mapper);
	   return reader;
   }
   
  /*
   @Bean
   protected ItemReader<Country> countryReader() {
	   log.info("COUNTRIES ITEM READER");
	   FlatFileItemReader<Country> reader = new FlatFileItemReader<Country>();
	   reader.setResource(new ClassPathResource("geonames/cities15000.txt"));
	   DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<Geoname>();
	   DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
	   tokenizer.setNames(new String[] {"ISO", "ISO3", "isoNumeric", "fips", "country", "capital", "areaSquareMiles", "population",
			   "continent", "tld", "currencyCode", "currencyName", "phone", "postalCodeFormat", "postalCodeRegex", "languages", "geonameid", "neighbours",
			   "equivalentFipsCode"}); 
	   mapper.setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB));
	   mapper.setFieldSetMapper(new GeonameFieldSetMapper());
	   reader.setLineMapper(mapper);
	   return reader;
   }
   */
}
