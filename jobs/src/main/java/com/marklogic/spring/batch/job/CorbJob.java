package com.marklogic.spring.batch.job;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.corb.CorbWriter;
import com.marklogic.spring.batch.item.MarkLogicItemReader;
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
public class CorbJob {

    @Autowired
    @Qualifier("target")
    DatabaseClientProvider databaseClientProvider;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilders.get("corbJob").start(step1).build();
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
        return new MarkLogicItemReader<String>(databaseClientProvider.getDatabaseClient(), "/ext/corb/uris.xqy");
    }

    @Bean
    public ItemProcessor<String, String> processor() {
        return new PassThroughItemProcessor<String>();
    }

    @Bean
    public ItemWriter<String> writer() {

        return new CorbWriter<>(databaseClientProvider.getDatabaseClient(), "/ext/corb/process.xqy");
    }


}
