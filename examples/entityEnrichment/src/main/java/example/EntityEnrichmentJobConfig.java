package example;

import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class EntityEnrichmentJobConfig extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
    private final String JOB_NAME = "entityEnrichmentJob";
    
    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(@Value("#{jobParameters['tokenizer_model']}") String tokenizerModel,
                     @Value("#{jobParameters['named_entity_model']}") String namedEntityModel) {
        
        ItemReader<CountedDistinctValue> reader = new ValuesItemReader(getDatabaseClient());
        ItemProcessor<CountedDistinctValue, String[]> processor =
            new EntityEnrichmentItemProcessor(getDatabaseClient(), tokenizerModel, namedEntityModel);
        ItemWriter<String[]> writer = new MarkLogicPatchItemWriter(getDatabaseClient());
           
        
        return stepBuilderFactory.get("step1")
                .<CountedDistinctValue, String[]>chunk(getChunkSize())
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
