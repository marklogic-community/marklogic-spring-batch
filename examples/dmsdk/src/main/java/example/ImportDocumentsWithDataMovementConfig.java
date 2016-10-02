package example;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.datamovement.DataMovementManager;
import com.marklogic.datamovement.WriteHostBatcher;
import com.marklogic.spring.batch.item.processor.ResourceToDocumentWriteOperationItemProcessor;
import com.marklogic.spring.batch.item.reader.EnhancedResourcesItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

@EnableBatchProcessing
public class ImportDocumentsWithDataMovementConfig implements EnvironmentAware {
    
    private Environment env;
    protected final Logger logger = LoggerFactory.getLogger(ImportDocumentsWithDataMovementConfig.class);
    private final String JOB_NAME = "importDocumentsWithDataMovementSdk";
    
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(
        StepBuilderFactory stepBuilderFactory,
        DatabaseClientProvider databaseClientProvider,
        @Value("#{jobParameters['output_collections']}") String[] collections,
        @Value("#{jobParameters['input_file_path']}") String inputFilePath,
        @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern) {
    
        ItemProcessor<Resource, DocumentWriteOperation> processor = new ResourceToDocumentWriteOperationItemProcessor();

        //DMSDK Initialization
        final DataMovementManager manager = DataMovementManager.newInstance();
        manager.withClient(databaseClientProvider.getDatabaseClient());
        WriteHostBatcher batcher = manager.newWriteHostBatcher();
        batcher.withJobName(JOB_NAME);
        batcher.onBatchSuccess((client, batch) -> {
            System.out.println("Sucessfully wrote " + batch.getItems().length);
        });
        batcher.onBatchFailure((client, batch, throwable) -> {
            System.err.println(throwable.toString());
        });
        manager.startJob(batcher);
    
        ItemWriter<DocumentWriteOperation> writer = new ItemWriter<DocumentWriteOperation>() {
            @Override
            public void write(List<? extends DocumentWriteOperation> items) throws Exception {
                logger.info("DMSDK ItemWriter");
                logger.info("Size: " + items.size());
                batcher.withBatchSize(items.size());
                for (DocumentWriteOperation item : items) {
                    DocumentMetadataHandle meta = new DocumentMetadataHandle();
                    meta.withCollections("monster");
                    batcher.add(UUID.randomUUID().toString(), meta, item.getContent());
                }
                batcher.flush();
            }
        };
                   
        return stepBuilderFactory.get("step1")
                .<Resource, DocumentWriteOperation>chunk(10)
                .reader(new EnhancedResourcesItemReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(writer)
                .build();
    }
     
    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
    
}
