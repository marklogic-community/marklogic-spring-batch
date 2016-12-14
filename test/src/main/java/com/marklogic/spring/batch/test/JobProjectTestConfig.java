package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.client.spring.SpringDatabaseClientConfig;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;


@Configuration
@PropertySource(value = "file:${job.home}/job.properties")
@PropertySource(value = "classpath:job.properties", ignoreResourceNotFound = true)
@Import(value = { SpringDatabaseClientConfig.class } )
public class JobProjectTestConfig {
    
    /**
     * Ensures that placeholders are replaced with property values
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public DatabaseClientProvider databaseClientProvider(DatabaseClientConfig databaseClientConfig) {
        return new SimpleDatabaseClientProvider(databaseClientConfig);
    }
    
    @Bean
    public XccTemplate xccTemplate(
            DatabaseClientConfig databaseClientConfig,
            @Value("${mlAppName}") String mlAppName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        databaseClientConfig.getUsername(),
                        databaseClientConfig.getPassword(),
                        databaseClientConfig.getHost(),
                        mlAppName + "-content"));
    }
    
}
