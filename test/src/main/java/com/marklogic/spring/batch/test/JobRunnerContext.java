package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.junit.ClientTestHelper;
import org.springframework.batch.test.JobLauncherTestUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunnerContext {

    @Bean
    public ClientTestHelper clientTestHelper(DatabaseClientConfig databaseClientConfig) {
        ClientTestHelper clientTestHelper = new ClientTestHelper();
        DatabaseClientProvider databaseClientProvider = new SimpleDatabaseClientProvider(databaseClientConfig);
        clientTestHelper.setDatabaseClientProvider(databaseClientProvider);
        clientTestHelper.setNamespaceProvider(new SpringBatchNamespaceProvider());
        return clientTestHelper;
    }

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();

    }
}
