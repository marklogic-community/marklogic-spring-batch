package com.marklogic.spring.batch.configuration;

import com.marklogic.client.helper.DatabaseClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JobProperties {

    private DatabaseClientConfig targetDatabaseClientConfig;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    @Autowired
    public JobProperties(
            @Value("${marklogic.host:localhost}") String host,
            @Value("${marklogic.port:8200}") int port,
            @Value("${marklogic.username:admin}") String username,
            @Value("${marklogic.password:admin}") String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        targetDatabaseClientConfig = new DatabaseClientConfig(host, port, username, password);
    }

    public DatabaseClientConfig getTargetDatabaseClientConfiguration() {

        return targetDatabaseClientConfig;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
