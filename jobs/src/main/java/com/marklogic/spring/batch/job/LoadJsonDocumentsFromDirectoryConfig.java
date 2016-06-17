package com.marklogic.spring.batch.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.JsonItemProcessor;
import com.marklogic.spring.batch.item.JsonItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;

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
                            ItemWriter<ObjectNode> writer, @Value("#{jobParameters['chunk'] ?: 100}") Integer chunkSize) {

        return stepBuilderFactory.get("jsonStep")
                .<Resource, ObjectNode>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Resource> reader(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern)
    {
        ResourcesItemReader itemReader = new ResourcesItemReader();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        try {
            Resource[] resources = ctx.getResources(inputFilePath);
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getFilename().matches(inputFilePattern)) {
                    resourceList.add(resources[i]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        itemReader.setResources(resourceList.toArray(new Resource[resourceList.size()]));
        return itemReader;
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
