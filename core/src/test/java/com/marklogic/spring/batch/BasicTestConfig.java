package com.marklogic.spring.batch;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource({ "file:../gradle.properties" })
public class BasicTestConfig extends com.marklogic.client.spring.BasicConfig {

    @Bean
    public DatabaseClientProvider databaseClientProvider(DatabaseClientConfig databaseClientConfig) {
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }
}
