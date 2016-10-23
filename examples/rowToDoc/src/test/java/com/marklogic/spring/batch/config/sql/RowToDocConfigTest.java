package com.marklogic.spring.batch.config.sql;

import com.marklogic.spring.batch.config.PathAwareColumnMapRowMapper;
import com.marklogic.spring.batch.config.RowToDocConfig;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Map;


/**
 * Handling data as a column map is perfect for a POC-style project where it's far more important to quickly get data
 * from a SQL database into MarkLogic than it is to generate precise XML. Using a column map avoids the need to create
 * Java objects and map them to tables.
 * 
 * This test class verifies the following:
 * <ol>
 * <li>Read rows with a JOIN as a column map (Map<String,Object>)</li>
 * <li>Process a forward slash in a column label to produce a nested XML element</li>
 * <li>Merge rows that have the same ID</li>
 * <li>Write merged rows as a single document to MarkLogic, with or without nested elements</li>
 * </ol>
 */
public class RowToDocConfigTest extends AbstractJobTest {

    protected static EmbeddedDatabase embeddedDatabase;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Before
    public void createDb() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL);
        builder.addScripts("db/sampledata_ddl.sql", "db/sampledata_insert.sql");
        embeddedDatabase = builder.build();
    }

    @After
    public void teardown() {
        if (embeddedDatabase != null) {
            embeddedDatabase.shutdown();
        }
        embeddedDatabase = null;
    }

    @Test
    public void jdbcCursorItemReaderTest() throws Exception {
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(embeddedDatabase);
        reader.setRowMapper(new PathAwareColumnMapRowMapper());
        String sql = "SELECT customer.id as \"customer/ID\" FROM customer";
        reader.setSql(sql);
        reader.open(new ExecutionContext());

        Map<String, Object> row = reader.read();
        for (String key : row.keySet()) {
            logger.info(key);
            //logger.info(Integer.toString((Map<String, Object>)row.get(key)));
        }
    }

    @Test
    public void runRowToDocJobTest() {
        runJob(RowToDocTestConfig.class,
                "--sql", "SELECT customer.*, invoice.id as \"invoice/id\", invoice.total as \"invoice/total\" FROM invoice LEFT JOIN customer on invoice.customerId = customer.id ORDER BY customer.id",
                "--jdbc_username", "sa",
                "--format", "xml",
                "--root_local_name", "invoice",
                "--collections", "invoice",
                "--transform_name", "simple",
                "--transform_parameters", "monster,grover,trash,oscar");

    }

    @Configuration
    public static class RowToDocTestConfig extends RowToDocConfig {
        @Override
        protected DataSource buildDataSource() {
            return embeddedDatabase;
        }
    }

}
