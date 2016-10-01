package example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.query.CountedDistinctValue;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@EnableBatchProcessing
public class EntityEnrichmentJobConfig implements EnvironmentAware {
    
    private Environment env;
    
    private final String JOB_NAME = "entityEnrichmentJob";
    
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(
        StepBuilderFactory stepBuilderFactory,
        DatabaseClientProvider databaseClientProvider,
        @Value("#{jobParameters['tokenizer_model']}") String tokenizerModel,
        @Value("#{jobParameters['named_entity_model']}") String namedEntityModel) {
        
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        ItemReader<CountedDistinctValue> reader = new ValuesItemReader(databaseClient);
        ItemProcessor<CountedDistinctValue, String[]> processor =
            new EntityEnrichmentItemProcessor(databaseClient, tokenizerModel, namedEntityModel);
        ItemWriter<String[]> writer = new MarkLogicPatchItemWriter(databaseClient);
           
        
        return stepBuilderFactory.get("step1")
                .<CountedDistinctValue, String[]>chunk(10)
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
