package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.RdfTripleItemReader;
import com.marklogic.spring.batch.item.RdfTripleItemWriter;
import org.apache.tika.Tika;
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
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sstafford on 6/17/2016.
 */
public class LoadImagesFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("loadImagesFromDirectoryJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(ItemReader<Resource> reader, ItemProcessor<Resource, Document> itemProcessor, ItemWriter<Document> itemWriter) {
        return stepBuilderFactory.get("step1")
                .<Resource, Document>chunk(getChunkSize())
                .reader(reader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Resource> reader(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath)
    {
        logger.info("READER");
        ResourcesItemReader itemReader = new ResourcesItemReader();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        try {
            Resource[] resources = ctx.getResources(inputFilePath);
            for (int i = 0; i < resources.length; i++) {
                logger.info(resources[i].getFilename());
                resourceList.add(resources[i]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        itemReader.setResources(resourceList.toArray(new Resource[resourceList.size()]));
        return itemReader;
    }

    @Bean
    public ItemProcessor<Resource, Document> itemProcessor() {
        return new ItemProcessor<Resource, Document>() {

            @Override
            public Document process(Resource item) throws Exception {
                logger.info("Processing images");
                Tika tika = new Tika();
                String output = "";
                try {
                    InputStream stream = item.getInputStream();
                    output = tika.parseToString(stream);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
                logger.info(output);
                return null;
            }
        };
    }

    @Bean
    public ItemWriter<Document> itemWriter() {
        return null;
    }
}
