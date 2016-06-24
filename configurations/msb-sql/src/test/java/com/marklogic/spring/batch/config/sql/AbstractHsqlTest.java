package com.marklogic.spring.batch.config.sql;

import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.After;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;

/**
 * Provides some support for tests that need to stand up an embedded HSQL database.
 */
@ContextConfiguration(classes = {JobProjectTestConfig.class})
public abstract class AbstractHsqlTest extends AbstractJobTest {

    protected static EmbeddedDatabase embeddedDatabase;

    @After
    public void teardown() {
        if (embeddedDatabase != null) {
            embeddedDatabase.shutdown();
        }
        embeddedDatabase = null;
    }

    protected void createDb(String... scripts) {
        embeddedDatabase = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScripts(scripts).build();
    }

}
