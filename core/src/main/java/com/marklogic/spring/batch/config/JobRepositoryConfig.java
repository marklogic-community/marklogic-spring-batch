package com.marklogic.spring.batch.config;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:job.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:job.properties", ignoreResourceNotFound = true)
public class JobRepositoryConfig {

    @Value("${marklogic.jobrepo.host}")
    private String jobRepoHost;

    @Value("${marklogic.jobrepo.port}")
    private int jobRepoPort;

    @Value("${marklogic.jobrepo.username}")
    private String jobRepoUsername;

    @Value("${marklogic.jobrepo.password}")
    private String jobRepoPassword;

    @Bean(name = "markLogicJobRepositoryDatabaseClientProvider")
    public DatabaseClientProvider databaseClientProvider() {
        DatabaseClientConfig databaseClientConfig = new DatabaseClientConfig(
                jobRepoHost, jobRepoPort, jobRepoUsername, jobRepoPassword);
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }

    @Bean
    @Conditional(UseMarkLogicBatchCondition.class)
    public BatchConfigurer batchConfigurer(
            @Qualifier(value = "markLogicJobRepositoryDatabaseClientProvider") DatabaseClientProvider databaseClientProvider) {
        return new MarkLogicBatchConfigurer(databaseClientProvider);
    }
}
