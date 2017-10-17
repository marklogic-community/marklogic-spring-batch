package com.marklogic.spring.batch.samples;

import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.item.reader.InvokeModuleItemReader;
import com.marklogic.spring.batch.item.writer.InvokeModuleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class })
public class CorbJobConfig {

    @Autowired
    DatabaseClientProvider databaseClientProvider;

    private final String JOB_NAME = "CORB";

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   @Qualifier("corbStep")Step corbStep) {
        return jobBuilderFactory.get(JOB_NAME).start(corbStep).build();
    }

    @Bean
    @JobScope
    public Step corbStep(StepBuilderFactory stepBuilderFactory,
                        DatabaseClientProvider databaseClientProvider,
                        @Value("#{jobParameters['URIS-MODULE']}") String urisModule,
                        @Value("#{jobParameters['PROCESS-MODULE']}") String processModule,
                         @Value("#{jobParameters['BATCH-SIZE']}") int batchSize) {
        ItemReader itemReader = new InvokeModuleItemReader<String>(databaseClientProvider.getDatabaseClient(), urisModule);
        ItemProcessor<String, Map<String, String>> itemProcessor = new ItemProcessor<String, Map<String, String>>() {

            @Override
            public Map<String, String> process(String item) throws Exception {
                Map<String, String> result = new HashMap<String, String>();
                result.put("URI", item);
                return result;
            }
        };
        ItemWriter itemWriter = new InvokeModuleItemWriter(databaseClientProvider.getDatabaseClient(), processModule);

        return stepBuilderFactory.get("step")
                .<String, Map<String, String>>chunk(batchSize)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }
}
