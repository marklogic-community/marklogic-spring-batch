package com.marklogic.spring.batch.item.rdbms;

import com.marklogic.spring.batch.item.rdbms.config.H2DatabaseConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.hamcrest.collection.IsMapContaining;

@ContextConfiguration(classes = {H2DatabaseConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AllTablesItemReaderTest  {

    JdbcTemplate jdbcTemplate;
    private EmbeddedDatabase db;
    private String databaseVendor = "";

    @Before
    public void before() {

        jdbcTemplate = new JdbcTemplate();

        //db = new EmbeddedDatabaseBuilder().addDefaultScripts().build();
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("db/sampledata.sql")
                .build();
    }
    @Test
    public void testTablesRows() throws Exception {

        AllTablesItemReader tablesItemReader = new AllTablesItemReader(db,databaseVendor);

        tablesItemReader.open(new ExecutionContext());

        Map<String, Object> tableRow1 = tablesItemReader.read();

        assertThat(tableRow1.size(),is(6));
        assertThat(tableRow1.get("FIRSTNAME").toString(),is("Laura"));
        assertThat(tableRow1, IsMapContaining.hasEntry("_tableName", "CUSTOMER"));

        Map<String, Object> tableRow2 = tablesItemReader.read();

        assertThat(tableRow2.size(),is(6));
        assertThat(tableRow2.get("FIRSTNAME").toString(),is("Susanne"));
        assertThat(tableRow2, IsMapContaining.hasEntry("_tableName", "CUSTOMER"));

        Map<String, Object> tableRow3 = tablesItemReader.read();

        assertThat(tableRow3.size(),is(6));
        assertThat(tableRow3.get("FIRSTNAME").toString(),is("Anne"));
        assertThat(tableRow3, IsMapContaining.hasEntry("_tableName", "CUSTOMER"));

        Map<String, Object> tableRow4 = tablesItemReader.read();

        assertThat(tableRow4.size(),is(6));
        assertThat(tableRow4.get("FIRSTNAME").toString(),is("Michael"));
        assertThat(tableRow4, IsMapContaining.hasEntry("_tableName", "CUSTOMER"));

        Map<String, Object> tableRow5 = tablesItemReader.read();

        assertThat(tableRow5.size(),is(6));
        assertThat(tableRow5.get("FIRSTNAME").toString(),is("Sylvia"));
        assertThat(tableRow5, IsMapContaining.hasEntry("_tableName", "CUSTOMER"));

        Map<String, Object> tableRow6 = tablesItemReader.read();

        assertThat(tableRow6.size(),is(4));
        assertThat(tableRow6, IsMapContaining.hasEntry("ID",0));
        assertThat(tableRow6, IsMapContaining.hasEntry("_tableName", "INVOICE"));

        Map<String, Object> tableRow7 = tablesItemReader.read();

        assertThat(tableRow7.size(),is(4));
        assertThat(tableRow7, IsMapContaining.hasEntry("ID",1));
        assertThat(tableRow7, IsMapContaining.hasEntry("_tableName", "INVOICE"));

        Map<String, Object> tableRow8 = tablesItemReader.read();

        assertThat(tableRow8.size(),is(6));
        assertThat(tableRow8, IsMapContaining.hasEntry("INVOICEID",0));
        assertThat(tableRow8, IsMapContaining.hasEntry("_tableName", "ITEM"));

        Map<String, Object> tableRow9 = tablesItemReader.read();

        assertThat(tableRow9.size(),is(4));
        assertThat(tableRow9, IsMapContaining.hasEntry("ID",0));
        assertThat(tableRow9, IsMapContaining.hasEntry("_tableName", "PRODUCT"));
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

}
