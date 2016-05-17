package com.marklogic.spring.batch.job;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.item.CollectionUrisReader;
import com.marklogic.spring.batch.item.DeleteUriWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import(com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class)
public class DeleteDocumentsJob {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    public DeleteDocumentsJob() {
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1) {

        return jobBuilders.get("deleteDocumentsJob").start(step1).build();
    }

    @Bean
    protected Step step1(ItemReader<String> reader, ItemProcessor<String, String> processor, ItemWriter<String> writer) {
        return stepBuilders.get("step1")
                .<String, String> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<String> reader() {
        CollectionUrisReader itemReader = new CollectionUrisReader(databaseClientProvider.getDatabaseClient(), "test");
        return itemReader;
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return new PassThroughItemProcessor<String>();
    }

    @Bean
    public ItemWriter<String> writer() {
        return new DeleteUriWriter(databaseClientProvider.getDatabaseClient());
    }

}
