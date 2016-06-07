package com.marklogic.spring.batch.configuration;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.Options;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Abstract class for a Spring Batch Configuration class that depends on a MarkLogic DatabaseClient.
 * The assumption is most MarkLogic Spring Batch configuration classes will need at least a JobBuilderFactory,
 * a StepBuilderFactory, and a DatabaseClient.
 */
@EnableBatchProcessing
public abstract class AbstractMarkLogicBatchConfig extends LoggingObject {

    @Autowired
    private Environment env;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    /**
     * Note that your subclass can introduce additional DatabaseClientProvider beans - just need to
     * give them different names, and then clients need to specify which ones they want.
     */
    @Autowired
    protected DatabaseClientProvider databaseClientProvider;

    /**
     * Convenience method for retrieving the "main" DatabaseClient instance.
     *
     * @return
     */
    protected DatabaseClient getDatabaseClient() {
        return databaseClientProvider.getDatabaseClient();
    }

    @Bean
    public BatchConfigurer jobRepositoryConfigurer() {
        return new JobRepositoryConfigurer();
    }

    public Integer getChunkSize() {
        String val = env.getProperty(Options.CHUNK_SIZE);
        return val != null ? Integer.parseInt(val) : 10;
    }

}
