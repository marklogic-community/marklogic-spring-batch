package com.marklogic.spring.batch.configuration;

import com.marklogic.client.helper.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JobProperties {

    public DatabaseClientConfig getTargetDatabaseClientConfiguration() {
        return targetDatabaseClientConfiguration;
    }

    DatabaseClientConfig targetDatabaseClientConfiguration;

    @Autowired
    public JobProperties(
            @Value("${marklogic.host:localhost}") String host,
            @Value("${marklogic.port:8200}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password) {
        targetDatabaseClientConfiguration = new DatabaseClientConfig(host, port, username, password);
    }

}
