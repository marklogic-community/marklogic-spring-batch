package com.marklogic.spring.batch.config;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class MarkLogicConfiguration {

    @Bean
    public DatabaseClientConfig databaseClientConfig(
            @Value("#{'${marklogic.host:localhost}'.split(',')}") List<String> hosts,
            @Value("${marklogic.port:8000}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Bean
    @Qualifier("targetDatabaseClientProvider")
    public DatabaseClientProvider databaseClientProvider(DatabaseClientConfig databaseClientConfig) {
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }
}
