package com.marklogic.spring.batch.samples;

import java.io.IOException;
import java.io.Writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.ext.helper.DatabaseClientProvider;

import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.item.reader.DocumentItemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * Simple MarkLogicReader job, This job calls the MarkLogicItemReader to evaluate a module using the
 * databaseclient and retrieves those URIS and writes to a file using FlatFileWriter.
 * The documents are ingested as part of the test setup
 * @author  Venugopal Iyengar
 * @version 1.0
 * @see EnableBatchProcessing
 */

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class})
public class ExportContentFromMarkLogicJobConfig {
    
    private Environment env; 
    
    private final String JOB_NAME = "WriteDocsToFileSystem";
    
    /**
     * The JobBuilderFactory and Step parameters are injected via Spring
     * @param jobBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param step injected from the step method in this class
     * @return Job bean
     */    
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step) {
        return jobBuilderFactory.get(JOB_NAME).start(step).build();
    }
    /**
     * The StepBuilderFactory and DatabaseClientProvider parameters are injected via Spring.  Custom parameters must be annotated with @Value.
     * @return Step
     * @param stepBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param databaseClientProvider injected from the BasicConfig class
     * @param collection This is an example of how user parameters could be injected via command line or a properties file
     * @see DatabaseClientProvider
     * @see com.marklogic.client.spring.BasicConfig
     * @see FlatFileItemWriter
     */
    
    @Bean
    @JobScope
    public Step step1(
        StepBuilderFactory stepBuilderFactory,
        DatabaseClientProvider databaseClientProvider,
        @Value("#{jobParameters['output_file_path']}") String outputFilePath,
        @Value("#{jobParameters['collection']}") String collection) throws Exception{
        
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        QueryManager qm = databaseClient.newQueryManager();

        StructuredQueryBuilder qb = qm.newStructuredQueryBuilder();

        StructuredQueryDefinition queryDef = qb.and(qb.collection(collection));
        DocumentItemReader itemReader = new DocumentItemReader(databaseClientProvider, queryDef);

        FlatFileItemWriter fileItemWriter = new FlatFileItemWriter<DocumentRecord>();
        fileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {

            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<data>");
            }
        });
        fileItemWriter.setFooterCallback(new FlatFileFooterCallback() {

            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("</data>");
            }
        });
        fileItemWriter.setEncoding("UTF-8");
        fileItemWriter.setLineAggregator(new LineAggregator<DocumentRecord>() {
            @Override
            public String aggregate(DocumentRecord item) {
                String content = "<record>\n";
                content += "<uri>" + item.getUri() + "</uri>\n";
                content += "<metadata>" + item.getMetadata(new StringHandle()).get().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "") + "</metadata>\n";
                content += item.getContent(new StringHandle()).get().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "") + "\n";
                content += "</record>";
                return content;
            }
        });


        MultiResourceItemWriter<DocumentRecord> itemWriter = new MultiResourceItemWriter<DocumentRecord>();
        itemWriter.setDelegate(fileItemWriter);
        itemWriter.setItemCountLimitPerResource(100);
        itemWriter.setResourceSuffixCreator(new ResourceSuffixCreator() {
            @Override
            public String getSuffix(int index) {
                return "-" + index + ".xml";
            }
        });
        itemWriter.setResource(new FileSystemResource(outputFilePath + "/output"));
        
        return stepBuilderFactory.get("step1")
                .<DocumentRecord, DocumentRecord>chunk(10)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    /*
    private class StringLineAggregator implements LineAggregator<String> {
    	private ObjectMapper objectMapper = new ObjectMapper();
    	@Override
    	public String aggregate(String item) {
    		try {
    			return objectMapper.writeValueAsString(item);
    		}
    		catch (JsonProcessingException e) {
    			throw new RuntimeException("Unable to serialize data", e);
    		}
    	}
    }
    */
    
}
