package com.marklogic.spring.batch.item.rdf;

import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;

@EnableBatchProcessing
@Import(MarkLogicBatchConfiguration.class)
public class ImportRdfFromFileJob {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("importRdfFromFileJob").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['graph_name']}") String graphName) {
        RdfTripleItemReader<Map<String, Object>> reader = new RdfTripleItemReader<Map<String, Object>>();
        reader.setFileName(inputFilePath);

        RdfTripleItemWriter writer = new RdfTripleItemWriter(databaseClientProvider.getDatabaseClient(), graphName);

        return stepBuilderFactory.get("step1")
                .<Map<String, Object>, Map<String, Object>>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
