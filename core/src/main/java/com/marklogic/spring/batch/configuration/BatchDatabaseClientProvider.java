package com.marklogic.spring.batch.configuration;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.DatabaseClientManager;
import org.springframework.beans.factory.DisposableBean;

/**
 * TODO Move this to ml-javaclient-util. The Simple implementation there is problematic because it
 * only allows for setting a DatabaseClientConfig via an Autowired annotation.
 */
public class BatchDatabaseClientProvider implements DatabaseClientProvider, DisposableBean {

    private DatabaseClientConfig config;
    private DatabaseClientManager manager;

    public BatchDatabaseClientProvider(DatabaseClientConfig config) {
        this.config = config;
    }

    @Override
    public DatabaseClient getDatabaseClient() {
        manager = new DatabaseClientManager();
        manager.setConfig(config);
        return manager.getObject();
    }

    @Override
    public void destroy() throws Exception {
        if (manager != null) {
            manager.destroy();
        }
    }
}
