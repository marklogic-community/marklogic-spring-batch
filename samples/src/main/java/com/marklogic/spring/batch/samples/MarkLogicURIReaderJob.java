package com.marklogic.spring.batch.samples;

import java.io.File;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import com.marklogic.spring.batch.item.reader.MarkLogicItemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
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
//@Import( { MarkLogicBatchConfigurer.class } )
public class MarkLogicURIReaderJob implements EnvironmentAware {
    
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
    public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step1) {
        return jobBuilderFactory.get(JOB_NAME).start(step1).build();
    }
    /**
     * The StepBuilderFactory and DatabaseClientProvider parameters are injected via Spring.  Custom parameters must be annotated with @Value.
     * @return Step
     * @param stepBuilderFactory injected from the @EnableBatchProcessing annotation
     * @param databaseClientProvider injected from the BasicConfig class
     * @param collections This is an example of how user parameters could be injected via command line or a properties file
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
        @Value("#{jobParameters['module_name']}") String moduleName) throws Exception{
        
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();                  
        MarkLogicItemReader<String> reader = new MarkLogicItemReader<String>(databaseClient, moduleName); 
        this.filePath = filePath;
        Assert.hasText(moduleName, "module_name cannot be null");
        
        return stepBuilderFactory.get("step1")
                .<String, String>chunk(10)
                .reader(reader)
                .writer(jsonItemWriter())
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
