package com.marklogic.spring.batch.test;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;


public class TestConfiguration {

    @Bean(name = "batchDatabaseClientConfig")
    public DatabaseClientConfig batchDatabaseClientConfig(
            @Value("#{'${marklogic.host}'.split(',')}") List<String> hosts,
            @Value("${marklogic.port:8000}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password) {
        return new DatabaseClientConfig(hosts.get(0), port, username, password);
    }

    @Bean
    @Qualifier("batchDatabaseClientProvider")
    public DatabaseClientProvider databaseClientProvider(
            @Qualifier("batchDatabaseClientConfig") DatabaseClientConfig batchDatabaseClientConfig) {
        return new SimpleDatabaseClientProvider(batchDatabaseClientConfig);

    }

    @Bean
    @Qualifier("batchXccTemplate")
    public XccTemplate xccTemplate(DatabaseClientConfig batchDatabaseClientConfig,
                                   @Value("${marklogic.database:Documents}") String databaseName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        batchDatabaseClientConfig.getUsername(),
                        batchDatabaseClientConfig.getPassword(),
                        batchDatabaseClientConfig.getHost(),
                        databaseName));
    }


}
