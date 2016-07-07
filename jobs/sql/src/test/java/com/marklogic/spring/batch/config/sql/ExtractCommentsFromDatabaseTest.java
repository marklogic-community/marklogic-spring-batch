package com.marklogic.spring.batch.config.sql;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.spring.batch.config.ExtractCommentsFromDatabaseConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

public class ExtractCommentsFromDatabaseTest extends AbstractHsqlTest {

    private ClientTestHelper testHelper;

    @Before
    public void setup() {
        testHelper = new ClientTestHelper();
        testHelper.setDatabaseClientProvider(getClientProvider());
        createDb("db/create-users-db.sql", "db/insert-users-with-comments.sql");
    }

    @Test
    public void readUsersViaSqlXmlQueryTest() {
        String sql = "SELECT comment FROM comments";
        runJobWithMarkLogicJobRepository(ExtractCommentsFromDatabaseTestConfig.class, "--sql", sql, "--output_collections", "abc");
        testHelper.assertCollectionSize("Expect 2 in abc collection", "abc", 2);
    }

    /**
     * With our embedded HSQL database, there's not a way that I know of for building a JDBC connection string for it.
     * So we override this method in the config class that we're testing to inject our own data source.
     */
    @Configuration
    public static class ExtractCommentsFromDatabaseTestConfig extends ExtractCommentsFromDatabaseConfig {
        @Override
        protected DataSource buildDataSource() {
            return embeddedDatabase;
        }
    }
}
