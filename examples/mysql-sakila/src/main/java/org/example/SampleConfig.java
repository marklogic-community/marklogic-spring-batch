package org.example;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.configuration.OptionParserConfigurer;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import joptsimple.OptionParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;

/**
 * Simple example of a custom configuration. Uses a reader that generates sample XML documents, and then
 * uses a writer that depends on Document instances. 
 */
@Configuration
public class SampleConfig extends AbstractMarkLogicBatchConfig implements OptionParserConfigurer {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("sampleJob").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters['count']}") Integer count) throws Exception {
        return stepBuilderFactory.get("step1")
                .<Document, Document>chunk(10)
                .reader(new SampleReader(count))
                .writer(new DocumentItemWriter(getDatabaseClient()))
                .build();
    }

    @Override
    public void configureOptionParser(OptionParser parser) {
        parser.accepts("count", "Number of sample documents to create").withRequiredArg();
    }
}
