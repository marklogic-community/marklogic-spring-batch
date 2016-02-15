package com.marklogic.spring.batch.sql;

import org.junit.After;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.marklogic.spring.batch.AbstractSpringBatchTest;

/**
 * Provides some support for tests that need to stand up an embedded HSQL database.
 */
public abstract class AbstractHsqlTest extends AbstractSpringBatchTest {

    protected EmbeddedDatabase db;

    @After
    public void teardown() {
        if (db != null) {
            db.shutdown();
        }
    }

    protected void createDb(String... scripts) {
        db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScripts(scripts).build();
    }

}
