package com.marklogic.spring.batch.config;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

@Configuration
@ComponentScan("com.marklogic.spring.batch.config")
public class MarkLogicBatchConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "markLogicJobRepositoryDatabaseClientConfig")
    public DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig(
            @Value("#{'${marklogic.jobrepo.host:localhost}'.split(',')}") List<String> hosts,
            @Value("${marklogic.jobrepo.port:8000}") int port,
            @Value("${marklogic.jobrepo.username:admin}") String username,
            @Value("${marklogic.jobrepo.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Bean
    @Qualifier("markLogicJobRepositoryDatabaseClientProvider")
    public DatabaseClientProvider markLogicJobRepositoryDatabaseClientProvider(
            @Qualifier("markLogicJobRepositoryDatabaseClientConfig")
                    DatabaseClientConfig marklogicJobRepositoryClientConfig) {
        return new SimpleDatabaseClientProvider(marklogicJobRepositoryClientConfig);
    }

    @Bean
    @Conditional(UseMarkLogicBatchCondition.class)
    public BatchConfigurer batchConfigurer(
            @Qualifier(value = "markLogicJobRepositoryDatabaseClientProvider") DatabaseClientProvider databaseClientProvider,
            BatchProperties batchProperties) {
        return new MarkLogicBatchConfigurer(databaseClientProvider, batchProperties);
    }

    @Bean
    @Qualifier("markLogicJobRepositoryXccTemplate")
    public XccTemplate markLogicJobRepositoryXccTemplate(DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig,
                                                         @Value("${marklogic.jobrepo.database:spring-batch}") String databaseName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        markLogicJobRepositoryDatabaseClientConfig.getUsername(),
                        markLogicJobRepositoryDatabaseClientConfig.getPassword(),
                        markLogicJobRepositoryDatabaseClientConfig.getHost(),
                        databaseName));
    }

}

