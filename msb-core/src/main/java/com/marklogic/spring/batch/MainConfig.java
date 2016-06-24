package com.marklogic.spring.batch;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.config.support.BatchDatabaseClientProvider;
import com.marklogic.spring.batch.config.support.MarkLogicJobRepositoryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Spring config class used by the Main program. The environment properties that this references
 * should all be based on constants defined in the Options class, which defines all of the recognized
 * command line options.
 */
@Configuration
public class MainConfig extends LoggingObject {

    @Autowired
    private Environment env;

    /**
     * Fetches connection properties from the Spring environment, which presumably has had command line
     * options added to it as properties.
     *
     * @return
     */
    @Bean
    public DatabaseClientProvider databaseClientProvider() {
        DatabaseClientConfig config = new DatabaseClientConfig(
                env.getProperty(Options.HOST),
                Integer.parseInt(env.getProperty(Options.PORT)),
                env.getProperty(Options.USERNAME),
                env.getProperty(Options.PASSWORD)
        );

        String db = env.getProperty(Options.DATABASE);
        if (db != null) {
            config.setDatabase(db);
        }

        String auth = env.getProperty(Options.AUTHENTICATION);
        if (auth != null) {
            config.setAuthentication(DatabaseClientFactory.Authentication.valueOfUncased(auth));
        }

        logger.info("Connecting to MarkLogic via: " + config);
        return new BatchDatabaseClientProvider(config);
    }

    /**
     * Conditionally instantiated bean that will be used by the MarkLogic JobRepository implementation
     * for connecting to MarkLogic. Defaults to using the "main" connection properties, in the likely
     * case that the JobRepository data should be stored in the same database that batch-processed
     * data is being stored in.
     *
     * @return
     */
    @Bean
    @Conditional(MarkLogicJobRepositoryCondition.class)
    public DatabaseClientProvider jobRepositoryDatabaseClientProvider() {
        DatabaseClientConfig config = new DatabaseClientConfig(
                selectProperty(Options.JOB_REPOSITORY_HOST, Options.HOST),
                Integer.parseInt(selectProperty(Options.JOB_REPOSITORY_PORT, Options.PORT)),
                selectProperty(Options.JOB_REPOSITORY_USERNAME, Options.USERNAME),
                selectProperty(Options.JOB_REPOSITORY_PASSWORD, Options.PASSWORD)
        );

        String db = env.getProperty(Options.JOB_REPOSITORY_DATABASE);
        if (db != null) {
            config.setDatabase(db);
        }

        String auth = env.getProperty(Options.JOB_REPOSITORY_AUTHENTICATION);
        if (auth != null) {
            config.setAuthentication(DatabaseClientFactory.Authentication.valueOfUncased(auth));
        }

        logger.info("Connecting to MarkLogic JobRepository via: " + config);
        return new BatchDatabaseClientProvider(config);
    }

    private String selectProperty(String preferredProperty, String defaultProperty) {
        String val = env.getProperty(preferredProperty);
        return val != null ? val : env.getProperty(defaultProperty);
    }
}
