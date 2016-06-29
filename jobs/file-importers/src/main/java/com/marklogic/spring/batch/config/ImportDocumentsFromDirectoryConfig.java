package com.marklogic.spring.batch.config;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.MarkLogicFileItemWriter;
import com.marklogic.spring.batch.item.MarkLogicImportItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Configuration
public class ImportDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("importDocumentsFromDirectoryJob").start(step).build();
    }

    @Bean
    @JobScope
    protected Step step(
        @Value("#{jobParameters['input_file_path']}") String inputFilePath,
        @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern,
        @Value("#{jobParameters['document_type']}") String documentType,
        @Value("#{jobParameters['output_uri_prefix']}") String outputUriPrefix,
        @Value("#{jobParameters['output_uri_replace']}") String outputUriReplace,
        @Value("#{jobParameters['output_uri_suffix']}") String outputUriSuffix,
        @Value("#{jobParameters['output_collections']}") String outputCollections) {
        return stepBuilderFactory.get("step")
                .<Resource, FileHandle>chunk(getChunkSize())
                .reader(reader(inputFilePath, inputFilePattern))
                .processor(processor(documentType))
                .writer(writer(outputCollections, outputUriPrefix, outputUriReplace, outputUriSuffix))
                .build();
    }

    public ItemReader<Resource> reader (String inputFilePath, String inputFilePattern) throws RuntimeException {
        if (inputFilePath == null) {
            throw new RuntimeException("input_file_path cannot be null");
        }
        inputFilePattern = (inputFilePattern == null) ? ".*" : inputFilePattern;
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

    public ItemProcessor<Resource, FileHandle> processor(String documentType) {
        MarkLogicImportItemProcessor processor = new MarkLogicImportItemProcessor();
        if (documentType != null) {
            processor.setFormat(Format.valueOf(documentType.toUpperCase()));
        }
        return processor;
    }

    public ItemWriter<FileHandle> writer(String outputCollections, String outputUriPrefix, String outputUriReplace,
        String outputUriSuffix) {
        MarkLogicFileItemWriter writer = new MarkLogicFileItemWriter(getDatabaseClient());
        writer.open(new ExecutionContext());
        writer.setCollections(outputCollections.split(","));
        writer.setOutputUriPrefix(outputUriPrefix);
        writer.setOutputUriReplace(outputUriReplace);
        writer.setOutputUriSuffix(outputUriSuffix);
        return writer;
    }

}
