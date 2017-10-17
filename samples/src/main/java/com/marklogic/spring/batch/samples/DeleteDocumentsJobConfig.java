package com.marklogic.spring.batch.samples;

import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.item.tasklet.DeleteDocumentsTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class})
public class DeleteDocumentsJobConfig  {
    
    @Autowired
    DatabaseClientProvider databaseClientProvider;

    @Bean
    @Primary
    public Job job(JobBuilderFactory jobBuilderFactory,
                   Step deleteDocumentsStep) {
        return jobBuilderFactory.get("deleteDocumentsJob").start(deleteDocumentsStep).build();
    }

    @Bean
    @JobScope
    public Step deleteDocumentsStep(StepBuilderFactory stepBuilderFactory,
                     DatabaseClientProvider databaseClientProvider,
                     @Value("#{jobParameters['output_collections']}") String[] collections,
                     @Value("#{jobParameters['marklogic_version'] ?: \"8\"}") String marklogicVersion) {
        StructuredQueryDefinition query = new StructuredQueryBuilder().collection(collections);
        DeleteDocumentsTasklet deleteDocumentsTasklet = new DeleteDocumentsTasklet(databaseClientProvider, query);
        deleteDocumentsTasklet.setMarklogicVersion(marklogicVersion);
        return stepBuilderFactory.get("Delete Documents").tasklet(deleteDocumentsTasklet).build();
    }
}
