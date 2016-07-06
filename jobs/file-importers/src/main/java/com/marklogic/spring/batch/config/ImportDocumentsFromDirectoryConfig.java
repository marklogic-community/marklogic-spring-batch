package com.marklogic.spring.batch.config;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.MarkLogicFileItemWriter;
import com.marklogic.spring.batch.item.MarkLogicImportItemProcessor;
import com.marklogic.spring.batch.item.file.MlcpFileReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
        writer.setCollections(outputCollections.split(","));
        writer.setOutputUriPrefix(outputUriPrefix);
        writer.setOutputUriReplace(outputUriReplace);
        writer.setOutputUriSuffix(outputUriSuffix);

        return stepBuilderFactory.get("step")
                .<Resource, FileHandle>chunk(getChunkSize())
                .reader(new MlcpFileReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(writer)
                .build();
    }
}
