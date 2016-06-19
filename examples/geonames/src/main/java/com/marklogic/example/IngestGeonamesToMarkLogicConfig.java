package com.marklogic.example;

import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.example.geonames.GeonameFieldSetMapper;
import com.marklogic.example.geonames.GeonamesItemProcessor;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.geonames.Geoname;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.w3c.dom.Document;


@Configuration
public class IngestGeonamesToMarkLogicConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("ingestGeonames").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(ItemReader<Geoname> reader, ItemProcessor<Geoname, Document> processor,
    ItemWriter<Document> writer, @Value("#{jobParameters['chunk'] ?: 100}") Integer chunkSize) {
        return stepBuilderFactory.get("step1")
                .<Geoname, Document> chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    protected ItemReader<Geoname> geonameReader() {
        FlatFileItemReader<Geoname> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("cities_us_east.txt"));
        DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        tokenizer.setQuoteCharacter('{');
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new GeonameFieldSetMapper());
        reader.setLineMapper(mapper);
        return reader;
    }

    @Bean
    protected ItemProcessor<Geoname, Document> processor() {
        return new GeonamesItemProcessor();
    }

    @Bean
    protected ItemWriter<Document> writer() {
        return new DocumentItemWriter(getDatabaseClient());
    }

    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(2);
        return taskExecutor;
    }
}