package com.marklogic.spring.batch.config;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.MarkLogicFileItemWriter;
import com.marklogic.spring.batch.item.MarkLogicImportItemProcessor;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ExecutionContext;
import com.marklogic.spring.batch.item.file.EnhancedResourcesItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import org.springframework.util.Assert;

@Configuration
public class ImportDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job importDocumentsFromDirectoryJob(@Qualifier("importDocumentsFromDirectoryStep1") Step step) {
        return jobBuilderFactory.get("importDocumentsFromDirectoryJob").start(step).build();
    }

    @Bean
    @JobScope
    public Step importDocumentsFromDirectoryStep1(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern,
            @Value("#{jobParameters['document_type']}") String documentType,
            @Value("#{jobParameters['output_uri_prefix']}") String outputUriPrefix,
            @Value("#{jobParameters['output_uri_replace']}") String outputUriReplace,
            @Value("#{jobParameters['output_uri_suffix']}") String outputUriSuffix,
            @Value("#{jobParameters['output_collections']}") String outputCollections) {

        Assert.hasText(inputFilePath, "input_file_path cannot be null");

        MarkLogicImportItemProcessor processor = new MarkLogicImportItemProcessor();
        if (documentType != null) {
            processor.setFormat(Format.valueOf(documentType.toUpperCase()));
        }

        MarkLogicFileItemWriter writer = new MarkLogicFileItemWriter(getDatabaseClient());
        writer.setUriGenerator(uriGenerator(outputUriPrefix, outputUriReplace, outputUriSuffix));
        writer.open(new ExecutionContext());
        if (outputCollections != null) {
            writer.setCollections(outputCollections.split(","));
        }

        return stepBuilderFactory.get("step")
                .<Resource, FileHandle>chunk(getChunkSize())
                .reader(new EnhancedResourcesItemReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(writer)
                .build();
    }

    public UriGenerator<String> uriGenerator(String outputUriPrefix, String outputUriReplace, String outputUriSuffix) {
        DefaultUriGenerator uriGen = new DefaultUriGenerator();
        uriGen.setOutputUriPrefix(outputUriPrefix);
        uriGen.setOutputUriReplace(outputUriReplace);
        uriGen.setOutputUriSuffix(outputUriSuffix);
        return uriGen;
    }
}
