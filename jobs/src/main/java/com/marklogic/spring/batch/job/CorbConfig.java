package com.marklogic.spring.batch.job;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.corb.CorbWriter;
import com.marklogic.spring.batch.item.MarkLogicItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorbConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("corbJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(
            @Value("#{jobParameters['urisModule']}") String urisModule,
            @Value("#{jobParameters['transformModule']}") String transformModule) {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(new MarkLogicItemReader<>(getDatabaseClient(), urisModule))
                .processor(new PassThroughItemProcessor<>())
                .writer(new CorbWriter<>(databaseClientProvider.getDatabaseClient(), transformModule))
                .build();
    }

}
