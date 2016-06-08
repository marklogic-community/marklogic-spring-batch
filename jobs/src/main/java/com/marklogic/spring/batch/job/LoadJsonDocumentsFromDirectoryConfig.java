package com.marklogic.spring.batch.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.JsonItemProcessor;
import com.marklogic.spring.batch.item.JsonItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.Resource;

/**
 * Created by sstafford on 6/7/2016.
 */
public class LoadJsonDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job jsonJob(@Qualifier("jsonStep") Step jsonStep) {
        return jobBuilderFactory.get("loadDocumentsFromDirectoryJob").start(jsonStep).build();
    }

    @Bean
    @JobScope
    protected Step jsonStep(ItemReader<Resource> reader, ItemProcessor<Resource, ObjectNode> processor,
                            ItemWriter<ObjectNode> writer, @Value("#{jobParameters['chunkSize'] ?: 100}") Integer chunkSize) {

        return stepBuilderFactory.get("jsonStep")
                .<Resource, ObjectNode>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemProcessor<Resource, ObjectNode> jsonProcessor() {
        return new JsonItemProcessor();
    }

    @Bean
    public ItemWriter<ObjectNode> jsonWriter() {
        return new JsonItemWriter(getDatabaseClient());
    }
}
