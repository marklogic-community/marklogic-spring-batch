package com.marklogic.spring.batch.samples;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.helper.DatabaseClientProvider;

import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.item.reader.DocumentItemReader;
import com.marklogic.spring.batch.item.reader.MarkLogicItemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Simple MarkLogicReader job, This job calls the MarkLogicItemReader to evaluate a module using the
 * databaseclient and retrieves those URIS and writes to a file using FlatFileWriter.
 * The documents are ingested as part of the test setup
 * @author  Venugopal Iyengar
 * @version 1.0
 * @see EnableBatchProcessing
 */

@EnableBatchProcessing
public class ExportContentFromMarkLogicJob implements EnvironmentAware {
    
    private Environment env; 
    
    private final String JOB_NAME = "Sample ML Reader Job";
    private final static String USER_HOME = "user.home";
    private final static String FILE_NAME_PREFIX = "sampleOutput";
    private final static String FILE_NAME_SUFFIX = ".txt";    
    private String filePath = null;
    
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
     * @see MarkLogicItemReader
     */
    
    @Bean
    @JobScope
    public Step step1(
        StepBuilderFactory stepBuilderFactory,
        DatabaseClientProvider databaseClientProvider,
        @Value("#{jobParameters['output_file_path']}") String filePath,
        @Value("#{jobParameters['collection']}") String collection) throws Exception{
        
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        QueryManager qm = databaseClient.newQueryManager();

        StructuredQueryBuilder qb = qm.newStructuredQueryBuilder();

        StructuredQueryDefinition queryDef = qb.and(qb.collection(collection));
        DocumentItemReader itemReader = new DocumentItemReader(databaseClientProvider, queryDef);

        FlatFileItemWriter fileItemWriter = new FlatFileItemWriter<DocumentRecord>();
        fileItemWriter.setEncoding("UTF-8");
        fileItemWriter.setLineAggregator(new LineAggregator<DocumentRecord>() {
            @Override
            public String aggregate(DocumentRecord item) {
                String content = "<record>";
                content += "<uri>" + item.getUri() + "</uri>";
                content += "</record>";
                return content;
            }
        });


        MultiResourceItemWriter<String> itemWriter = new MultiResourceItemWriter<String>();
        itemWriter.setDelegate(fileItemWriter);
        itemWriter.setItemCountLimitPerResource(100);
        itemWriter.setResourceSuffixCreator(new ResourceSuffixCreator() {
            @Override
            public String getSuffix(int index) {
                return Integer.toString(Math.floorDiv(index, 100));
            }
        });
        itemWriter.setResource(new FileSystemResource("c:\\temp\\output"));
        
        return stepBuilderFactory.get("step1")
                .<DocumentRecord, String>chunk(10)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }     
    
    @Bean
    @JobScope
    public FlatFileItemWriter<String> jsonItemWriter() throws Exception {    	
        FlatFileItemWriter<String> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setLineAggregator(new StringLineAggregator());
        Assert.notNull(filePath, "output_file_path must not be null");
        //System.out.println(">>>> output file path [" + filePath + "]");
        String sampleOutputPath = File.createTempFile(FILE_NAME_PREFIX, FILE_NAME_SUFFIX, createDirectory(filePath)).getAbsolutePath();
        //System.out.println(">>> Output Path: " + sampleOutputPath);
        itemWriter.setResource(new FileSystemResource(sampleOutputPath));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
           
    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
    
    public File createDirectory(String directoryName) {
    	String path = System.getProperty(USER_HOME);
    	File dir=new File(path + File.separator + directoryName);
    	dir.mkdir();
    	return dir;
    };
    
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
    
}
