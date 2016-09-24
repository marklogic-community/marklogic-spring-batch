package com.marklogic.spring.batch.config;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.config.support.OptionParserConfigurer;
import com.marklogic.spring.batch.corb.CorbWriter;
import com.marklogic.spring.batch.item.reader.MarkLogicItemReader;
import joptsimple.OptionParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
public class CorbConfig implements OptionParserConfigurer {
    
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    protected StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    DatabaseClientProvider databaseClientProvider;

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("corbJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(
            @Value("#{jobParameters['uris_module']}") String urisModule,
            @Value("#{jobParameters['transform_module']}") String transformModule) {
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(new MarkLogicItemReader<>(databaseClientProvider.getDatabaseClient(), urisModule))
                .processor(new PassThroughItemProcessor<>())
                .writer(new CorbWriter<>(databaseClientProvider.getDatabaseClient(), transformModule))
                .build();
    }

    @Override
    public void configureOptionParser(OptionParser parser) {
        parser.accepts("uris_module", "Path of the URIs module").withRequiredArg();
        parser.accepts("transform_module", "Path of the transform module").withRequiredArg();
    }
}
