package com.marklogic.spring.batch.item.file;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.WriteBatch;
import com.marklogic.client.datamovement.WriteBatchListener;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.spring.batch.item.processor.MarkLogicItemProcessor;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import org.junit.rules.TemporaryFolder;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.UUID;

@EnableBatchProcessing
@Import(value = {
        com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class,
        com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
@PropertySource("classpath:job.properties")
public class EnhancedResourceJobConfig {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    // This is the bean label for the name of your Job.  Pass this label into the job_id parameter
    // when using the CommandLineJobRunner
    private final String JOB_NAME = "enhancedResourcesJob";

    private TemporaryFolder file = new TemporaryFolder();

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
                jobExecution.getExecutionContext().putString("random", "enhancedResourcesJob123");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                logger.info("AFTER JOB");

                file.delete();
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

        try {
            file.create();

            file.newFolder("A");
            file.newFolder("B");
            file.newFolder("A", "A1");

            file.newFile("/A/red.txt");
            file.newFile("/B/blue.jpg");
            file.newFile("/B/yellow.txt");
            file.newFile("/A/A1/green.txt");
        }
        catch (IOException e) {
            logger.error("Cannot create temporary files", e);
        }
        logger.info("Created temporary directory for test files: " + file.getRoot().getAbsolutePath());

        EnhancedResourcesItemReader reader = new EnhancedResourcesItemReader();
        reader.setInputFilePath(file.getRoot().getAbsolutePath());
        //reader.setInputFilePath("C:\\Users\\Glenn\\AppData\\Local\\Temp\\junit2596835750051196063");
        //reader.setInputFilePath("D:\\junit2596835750051196063");
        //reader.setInputFilePath("D:/tmp/dirtree");
        // we want to read all text files only
        reader.setInputFilePattern(".*\\.txt");

        //The ItemProcessor is typically customized for your Job.  An anoymous class is a nice way to instantiate but
        //if it is a reusable component instantiate in its own class
        MarkLogicItemProcessor<Resource> processor = new MarkLogicItemProcessor<Resource>() {

            @Override
            public DocumentWriteOperation process(Resource item) throws Exception {
                DocumentWriteOperation dwo = new DocumentWriteOperation() {

                    @Override
                    public OperationType getOperationType() {
                        return OperationType.DOCUMENT_WRITE;
                    }

                    @Override
                    public String getUri() {
                        return UUID.randomUUID().toString() + "-" + item.getFilename();
                    }

                    @Override
                    public DocumentMetadataWriteHandle getMetadata() {
                        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                        metadata.withCollections(collections);
                        return metadata;
                    }

                    @Override
                    public AbstractWriteHandle getContent()  {
                        try {
                            return new FileHandle(item.getFile());
                        }
                        catch (IOException e) {
                            return null;
                        }
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
        writer.setWriteBatchListener(new WriteBatchListener() {
            @Override
            public void processEvent(WriteBatch batch) {
                logger.info("Writing batch");
            }
        });

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
                .<Resource, DocumentWriteOperation>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(chunkListener)
                .build();
    }


}
