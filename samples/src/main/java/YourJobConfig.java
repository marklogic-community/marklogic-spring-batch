import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.spring.batch.item.processor.MarkLogicItemProcessor;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

/**
 * YourJobConfig.java - a sample Spring Batch configuration class for demonstrating the use of creating a SpringBatch job
 * running with MarkLogic.  By default it uses a in-memory JobRepository.  Remove the comment to import the MarkLogicBatchConfigurer
 * to utilize the MarkLogic JobRepository.
 *
 * @author Scott Stafford
 * @version 1.3.0
 * @see EnableBatchProcessing
 * @see com.marklogic.spring.batch.config.MarkLogicBatchConfiguration
 */

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class})
public class YourJobConfig {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // This is the bean label for the name of your Job.  Pass this label into the job_id parameter
    // when using the CommandLineJobRunner
    private final String JOB_NAME = "yourJob";

    /**
     * The JobBuilderFactory and Step parameters are injected via the EnableBatchProcessing annotation.
     *
     * @param jobBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param step              injected from the step method in this class
     * @return Job
     */
    @Bean(name = JOB_NAME)
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        JobExecutionListener listener = new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                logger.info("BEFORE JOB");
                jobExecution.getExecutionContext().putString("random", "yourJob123");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                logger.info("AFTER JOB");
            }
        };

        return jobBuilderFactory.get(JOB_NAME)
                .start(step)
                .listener(listener)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    /**
     * The StepBuilderFactory and DatabaseClientProvider parameters are injected via Spring.  Custom parameters must be annotated with @Value.
     *
     * @param stepBuilderFactory     injected from the @EnableBatchProcessing annotation
     * @param databaseClientProvider injected from the BasicConfig class
     * @param collections            This is an example of how user parameters could be injected via command line or a properties file
     * @return Step
     * @see DatabaseClientProvider
     * @see ItemReader
     * @see ItemProcessor
     * @see DocumentWriteOperation
     * @see MarkLogicItemProcessor
     * @see MarkLogicItemWriter
     */

    @Bean
    @JobScope
    public Step step(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['output_collections'] ?: 'yourJob'}") String[] collections,
            @Value("#{jobParameters['chunk_size'] ?: 20}") int chunkSize) {

        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();

        ItemReader<String> reader = new ItemReader<String>() {
            int i = 0;

            @Override
            public String read() throws Exception {
                i++;
                return i <= 100 ? "hello" : null;
            }
        };

        //The ItemProcessor is typically customized for your Job.  An anoymous class is a nice way to instantiate but
        //if it is a reusable component instantiate in its own class
        MarkLogicItemProcessor<String> processor = new MarkLogicItemProcessor<String>() {

            @Override
            public DocumentWriteOperation process(String item) throws Exception {
                DocumentWriteOperation dwo = new DocumentWriteOperation() {

                    @Override
                    public OperationType getOperationType() {
                        return OperationType.DOCUMENT_WRITE;
                    }

                    @Override
                    public String getUri() {
                        return UUID.randomUUID().toString() + ".json";
                    }

                    @Override
                    public DocumentMetadataWriteHandle getMetadata() {
                        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                        metadata.withCollections(collections);
                        return metadata;
                    }

                    @Override
                    public AbstractWriteHandle getContent() {
                        return new StringHandle(String.format("{ \"name\" : \"%s\" }", item));
                    }

                    @Override
                    public String getTemporalDocumentURI() {
                        return null;
                    }
                };
                return dwo;
            }
        };

        MarkLogicItemWriter writer = new MarkLogicItemWriter(databaseClient);
        writer.setBatchSize(chunkSize);


        ChunkListener chunkListener = new ChunkListener() {

            @Override
            public void beforeChunk(ChunkContext context) {
                logger.info("beforeChunk");
            }

            @Override
            public void afterChunk(ChunkContext context) {
                logger.info("afterChunk");
            }

            @Override
            public void afterChunkError(ChunkContext context) {

            }
        };

        return stepBuilderFactory.get("step1")
                .<String, DocumentWriteOperation>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(chunkListener)
                .build();
    }

}
