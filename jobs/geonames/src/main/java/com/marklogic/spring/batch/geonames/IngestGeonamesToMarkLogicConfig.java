package com.marklogic.spring.batch.geonames;

import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.geonames.Geoname;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.w3c.dom.Document;


@Configuration
public class IngestGeonamesToMarkLogicConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("ingestGeonamesStep1") Step step1) {
        return jobBuilderFactory.get("ingestGeonames").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step ingestGeonamesStep1() {
        FlatFileItemReader<Geoname> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("cities_us_east.txt"));
        DefaultLineMapper<Geoname> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        tokenizer.setQuoteCharacter('{');
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new GeonameFieldSetMapper());
        reader.setLineMapper(mapper);

        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(2);

        return stepBuilderFactory.get("step1")
                .<Geoname, Document>chunk(getChunkSize())
                .reader(reader)
                .processor(new GeonamesItemProcessor())
                .writer(new DocumentItemWriter(getDatabaseClient()))
                .taskExecutor(taskExecutor)
                .build();
    }
}