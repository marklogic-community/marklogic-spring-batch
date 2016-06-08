package com.marklogic.spring.batch.config.sql;

import com.marklogic.spring.batch.job.AbstractJobTest;
import org.junit.After;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Provides some support for tests that need to stand up an embedded HSQL database.
 */
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
