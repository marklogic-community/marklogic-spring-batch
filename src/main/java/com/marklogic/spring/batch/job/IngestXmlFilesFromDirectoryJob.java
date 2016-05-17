package com.marklogic.spring.batch.job;

import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;

import java.io.IOException;

@Configuration
@EnableBatchProcessing
@Import(com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class)
public class IngestXmlFilesFromDirectoryJob {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    private Resource[] resources;

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilders.get("myJob").start(step1).build();
    }

    @Bean
    protected Step step1(ItemReader<Resource> reader, ItemProcessor<Resource, Document> processor, ItemWriter<Document> writer) {
        return stepBuilders.get("step1")
                .<Resource, Document> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Resource> reader() {
        ResourcesItemReader itemReader = new ResourcesItemReader();
        loadResources();
        itemReader.setResources(resources);
        return itemReader;
    }

    @Bean
    public ItemProcessor<Resource, Document> processor() {
        return null;
    }

    @Bean
    public ItemWriter<Document> writer() {
        return new DocumentItemWriter();
    }

    private void loadResources() {
        try {
            resources = ctx.getResources("file:E:\\world-bank\\marklogic-spring-batch\\src\\test\\resources\\data\\*.xml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}

