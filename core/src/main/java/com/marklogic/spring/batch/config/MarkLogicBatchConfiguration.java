package com.marklogic.spring.batch.config;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

@Configuration
@ComponentScan(
        basePackageClasses = {
                com.marklogic.spring.batch.config.MarkLogicBatchConfigurer.class,
                com.marklogic.spring.batch.core.repository.support.MarkLogicJobRepositoryProperties.class
        })
public class MarkLogicBatchConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Conditional(UseMarkLogicBatchCondition.class)
    @Bean(name = "markLogicJobRepositoryDatabaseClientConfig")
    @Qualifier("markLogicJobRepositoryDatabaseClientConfig")
    public DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig(
            @Value("#{'${marklogic.jobrepo.host:localhost}'.split(',')}") List<String> hosts,
            @Value("${marklogic.jobrepo.port:8201}") int port,
            @Value("${marklogic.jobrepo.username:admin}") String username,
            @Value("${marklogic.jobrepo.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Conditional(UseMarkLogicBatchCondition.class)
    @Bean(name = "markLogicJobRepositoryDatabaseClientProvider")
    @Qualifier("markLogicJobRepositoryDatabaseClientProvider")
    public DatabaseClientProvider markLogicJobRepositoryDatabaseClientProvider(
            @Qualifier("markLogicJobRepositoryDatabaseClientConfig")
                    DatabaseClientConfig marklogicJobRepositoryClientConfig) {
        return new SimpleDatabaseClientProvider(marklogicJobRepositoryClientConfig);
    }
}

