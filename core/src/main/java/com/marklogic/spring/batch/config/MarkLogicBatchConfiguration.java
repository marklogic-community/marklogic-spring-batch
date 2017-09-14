package com.marklogic.spring.batch.config;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

@Configuration
@PropertySource(value = "classpath:job.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:job.properties", ignoreResourceNotFound = true)
public class MarkLogicBatchConfiguration {

    @Bean(name = "batchDatabaseClientConfig")
    public DatabaseClientConfig batchDatabaseClientConfig(
            @Value("#{'${marklogic.host}'.split(',')}") List<String> hosts,
            @Value("${marklogic.port:8000}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Bean(name = "markLogicJobRepositoryDatabaseClientConfig")
    public DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig (
            @Value("#{'${marklogic.jobrepo.host:localhost}'.split(',')}") List<String> hosts,
            @Value("${marklogic.jobrepo.port:8000}") int port,
            @Value("${marklogic.jobrepo.username:admin}") String username,
            @Value("${marklogic.jobrepo.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Bean
    @Qualifier("batchDatabaseClientProvider")
    public DatabaseClientProvider databaseClientProvider(
            @Qualifier("batchDatabaseClientConfig") DatabaseClientConfig batchDatabaseClientConfig) {
        return new SimpleDatabaseClientProvider(batchDatabaseClientConfig);

    }

    @Bean
    @Qualifier("markLogicJobRepositoryDatabaseClientProvider")
    public DatabaseClientProvider markLogicJobRepositoryDatabaseClientProvider(
            @Qualifier("markLogicJobRepositoryDatabaseClientConfig")
                    DatabaseClientConfig marklogicJobRepositoryClientConfig) {
        return new SimpleDatabaseClientProvider(marklogicJobRepositoryClientConfig);
    }

    @Bean
    @Qualifier("markLogicJobRepositoryDatabaseClientProvider")
    @Conditional(UseMarkLogicBatchCondition.class)
    public BatchConfigurer batchConfigurer(
            @Qualifier(value = "markLogicJobRepositoryDatabaseClientProvider") DatabaseClientProvider databaseClientProvider) {
        return new MarkLogicBatchConfigurer(databaseClientProvider);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Qualifier("batchDatabaseClientConfig")
    public XccTemplate xccTemplate(DatabaseClientConfig batchDatabaseClientConfig,
                                   @Value("${marklogic.database:Documents}") String databaseName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        batchDatabaseClientConfig.getUsername(),
                        batchDatabaseClientConfig.getPassword(),
                        batchDatabaseClientConfig.getHost(),
                        databaseName));
    }

    @Bean
    @Qualifier("markLogicJobRepositoryDatabaseClientConfig")
    public XccTemplate jobRepoXccTemplate(DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig,
                                          @Value("${marklogic.jobrepo.database:spring-batch}") String databaseName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        markLogicJobRepositoryDatabaseClientConfig.getUsername(),
                        markLogicJobRepositoryDatabaseClientConfig.getPassword(),
                        markLogicJobRepositoryDatabaseClientConfig.getHost(),
                        databaseName));
    }

}
