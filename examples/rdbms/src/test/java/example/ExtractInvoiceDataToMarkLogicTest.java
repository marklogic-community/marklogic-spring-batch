package example;

import com.marklogic.spring.batch.config.support.BatchDatabaseClientProvider;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

@ContextConfiguration(classes = {JobProjectTestConfig.class})
public class ExtractInvoiceDataToMarkLogicTest extends AbstractJobTest {
    
    protected static EmbeddedDatabase embeddedDatabase;
    
    @Before
    public void setup() {
        createDb("db/sampledata_ddl.sql", "db/sampledata_insert.sql");
    }
    
    @Test
    public void verifySampleDatabase() {
        
        assertTrue(true);
    }
        
    @After
    public void teardown() {
        if (embeddedDatabase != null) {
            embeddedDatabase.shutdown();
        }
        embeddedDatabase = null;
    }
    
    protected void createDb(String... scripts) {
        embeddedDatabase = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScripts(scripts).build();
        BatchDatabaseClientProvider provider = null;
    }
    
    /**
     * With our embedded HSQL database, there's not a way that I know of for building a JDBC connection string for it.
     * So we override this method in the config class that we're testing to inject our own data source.
     */
    @Configuration
    public static class ExtractCommentsFromDatabaseTestConfig extends ExtractInvoiceDataToMarkLogicConfig {
        @Override
        protected DataSource buildDataSource() {
            return embeddedDatabase;
        }
    }
    

}