package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.configuration.OptionParserConfigurer;
import com.marklogic.spring.batch.item.CollectionUrisReader;
import com.marklogic.spring.batch.item.DeleteUriWriter;
import joptsimple.OptionParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In order to reference job parameters, the step is defined at the job scope and the reader is defined
 * at the step scope.
 */
@Configuration
public class DeleteDocumentsConfig extends AbstractMarkLogicBatchConfig implements OptionParserConfigurer {

    @Override
    public void configureOptionParser(OptionParser parser) {
        parser.accepts("collections", "Comma-separated list of collections containing documents to delete").withRequiredArg();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("deleteDocumentsJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(ItemReader<String> reader, ItemWriter<String> writer) {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(getChunkSize())
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public CollectionUrisReader reader(@Value("#{jobParameters['collections']}") String[] collections) {
        return new CollectionUrisReader(getDatabaseClient(), collections);
    }

    @Bean
    public ItemWriter<String> writer() {
        return new DeleteUriWriter(getDatabaseClient());
    }

}
