package org.example;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestGeonamesToMarkLogicConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("corbJob").start(step1).build();
    }

}