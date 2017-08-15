package com.marklogic.spring.batch.samples;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


/**
 * YourJobConfig.java - a sample Spring Batch configuration class for demonstrating the use of creating a SpringBatch job
 * running with MarkLogic.  By default it uses a in-memory JobRepository.  Remove the comment to import the MarkLogicBatchConfigurer
 * to utilize the MarkLogic JobRepository.
 * @author  Scott Stafford
 * @version 1.0
 * @see EnableBatchProcessing
 */

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class })
public class YourJobConfig {

    //Rename this private variable
    private final String JOB_NAME = "yourJob";
    
    /**
     * The JobBuilderFactory and Step parameters are injected via Spring
     * @param jobBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param step injected from the step method in this class
     * @return Job bean
     */
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }
    
    /**
     * The StepBuilderFactory and DatabaseClientProvider parameters are injected via Spring.  Custom parameters must be annotated with @Value.
     * @return Step
     * @param stepBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param databaseClientProvider injected from the BasicConfig class
     * @param collections This is an example of how user parameters could be injected via command line or a properties file
     * @see DatabaseClientProvider
     * @see com.marklogic.client.spring.BasicConfig
     * @see ItemReader
     * @see ItemProcessor
     * @see MarkLogicItemWriter
     */
    
    @Bean
    @JobScope
    public Step step(
        StepBuilderFactory stepBuilderFactory,
        DatabaseClientProvider databaseClientProvider,
        @Value("#{jobParameters['output_collections'] ?: 'yourJob'}") String[] collections) {
        
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
            
        ItemReader<String> reader = new ItemReader<String>() {
            int i = 0;
            @Override
            public String read() throws Exception {
                i++;
                return i == 1 ? "hello" : null;
            }
        };
        
        //The ItemProcessor is typically customized for your Job.  An anoymous class is a nice way to instantiate but
        //if it is a reusable component instantiate in its own class
        ItemProcessor<String, DocumentWriteOperation> processor = new ItemProcessor<String, DocumentWriteOperation>() {
    
            @Override
            public DocumentWriteOperation process(String item) throws Exception {
                String uri = "/hello.xml";
                
                String xml = "<message>" + item + "</message>";
                StringHandle handle = new StringHandle(xml);
                
                DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                metadata.withCollections(collections);
                
                return new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                        uri, metadata, handle);
            }
        };
        ItemWriter<DocumentWriteOperation> writer = new MarkLogicItemWriter(databaseClient);
        return stepBuilderFactory.get("step1")
                .<String, DocumentWriteOperation>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
