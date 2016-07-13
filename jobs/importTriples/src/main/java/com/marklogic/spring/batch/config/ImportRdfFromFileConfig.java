package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.columnmap.PathAwareColumnMapProcessor;
import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.RdfTripleItemReader;
import com.marklogic.spring.batch.item.RdfTripleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ImportRdfFromFileConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("importRdfFromFileJob").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['graph_name']}") String graphName) {
        RdfTripleItemReader<Map<String, Object>> reader = new RdfTripleItemReader<Map<String, Object>>();
        reader.setFileName(inputFilePath);

        RdfTripleItemWriter writer = new RdfTripleItemWriter(getDatabaseClient(), graphName);

        return stepBuilderFactory.get("step1")
                .<Map<String, Object>, Map<String, Object>>chunk(getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }
}
