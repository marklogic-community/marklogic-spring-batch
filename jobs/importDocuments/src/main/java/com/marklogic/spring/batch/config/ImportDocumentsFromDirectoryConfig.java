package com.marklogic.spring.batch.config;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.MarkLogicFileItemWriter;
import com.marklogic.spring.batch.item.MarkLogicImportItemProcessor;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import com.marklogic.spring.batch.item.ResourceToDocumentRecordItemProcessor;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import com.marklogic.spring.batch.item.file.EnhancedResourcesItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
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

        ResourceToDocumentRecordItemProcessor processor = new ResourceToDocumentRecordItemProcessor();
        if (documentType != null) {
            processor.setFormat(Format.valueOf(documentType.toUpperCase()));
        }
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(outputCollections.split(","));
        processor.setMetadataHandle(metadata);

        MarkLogicItemWriter writer = new MarkLogicItemWriter(getDatabaseClient());

        return stepBuilderFactory.get("step")
                .<Resource, DocumentWriteOperation>chunk(getChunkSize())
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
