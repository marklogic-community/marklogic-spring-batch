package example;

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

public class YourJobConfig extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
    private final String JOB_NAME = "yourJob";
    
    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(
            @Value("#{jobParameters['output_collections']}") String[] collections) {
            
        ItemReader<String> reader = null;
        ItemProcessor<String, String> processor = null;
        ItemWriter<String> writer = null;
    
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(getChunkSize())
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
