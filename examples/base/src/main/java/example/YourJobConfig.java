package example;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

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
            
        ItemReader<String> reader = new ItemReader<String>() {
            int i = 0;
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                i++;
                return i == 1 ? "hello" : null;
            }
        };
        
        ItemProcessor<String, DocumentWriteOperation> processor = new ItemProcessor<String, DocumentWriteOperation>() {
    
            @Override
            public DocumentWriteOperation process(String item) throws Exception {
                String uri = "/hello.xml";
                
                String xml = "<message>" + item + "</message>";
                StringHandle handle = new StringHandle(xml);
                
                DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                metadata.withCollections(collections);
                
                return new MarkLogicWriteHandle(uri, metadata, handle);
            }
        };
        ItemWriter<DocumentWriteOperation> writer = new MarkLogicItemWriter(getDatabaseClient());
    
        return stepBuilderFactory.get("step1")
                .<String, DocumentWriteOperation>chunk(getChunkSize())
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
