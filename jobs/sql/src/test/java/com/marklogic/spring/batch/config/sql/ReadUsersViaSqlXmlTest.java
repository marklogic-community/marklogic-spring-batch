package com.marklogic.spring.batch.config.sql;

import com.marklogic.spring.batch.config.ExtractUsersFromDatabaseConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

public class ReadUsersViaSqlXmlTest extends AbstractHsqlTest {

    @Before
    public void setup() {
        createDb("db/create-users-db.sql", "db/insert-users-with-comments.sql");
    }

    @Test
    public void readUsersViaSqlXmlQueryTest() {
        String sql = "SELECT comment FROM comments";
        runJobWithMarkLogicJobRepository(ExtractUsersFromDatabaseTestConfig.class, "--sql", sql, "--root_local_name", "user");
    }

    /**
     * With our embedded HSQL database, there's not a way that I know of for building a JDBC connection string for it.
     * So we override this method in the config class that we're testing to inject our own data source.
     */
    @Configuration
    public static class ExtractUsersFromDatabaseTestConfig extends ExtractUsersFromDatabaseConfig {
        @Override
        protected DataSource buildDataSource() {
            return embeddedDatabase;
        }
    }
}
