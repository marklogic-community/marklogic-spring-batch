package example;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.datamovement.DataMovementManager;
import com.marklogic.datamovement.WriteHostBatcher;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.processor.ResourceToDocumentWriteOperationItemProcessor;
import com.marklogic.spring.batch.item.reader.EnhancedResourcesItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

public class ImportDocumentsWithDataMovementConfig extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
    private final String JOB_NAME = "importDocumentsWithDataMovementSdk";
    
    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(
            @Value("#{jobParameters['output_collections']}") String[] collections,
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern) {
    
        ItemProcessor<Resource, DocumentWriteOperation> processor = new ResourceToDocumentWriteOperationItemProcessor();

        //DMSDK Initialization
        final DataMovementManager manager = DataMovementManager.newInstance();
        manager.setClient(getDatabaseClient());
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
                //batcher.awaitCompletion(1000, TimeUnit.MILLISECONDS);
                batcher.flush();
            }
        };
                   
        return stepBuilderFactory.get("step1")
                .<Resource, DocumentWriteOperation>chunk(getChunkSize())
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
