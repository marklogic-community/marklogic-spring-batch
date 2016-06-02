package com.marklogic.spring.batch.sql;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.columnmap.PathAwareColumnMapProcessor;
import com.marklogic.spring.batch.item.ColumnMapItemWriter;

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
public class ReadAndWriteColumnMapsTest extends AbstractHsqlTest {

    private JdbcCursorItemReader<Map<String, Object>> reader;

    @Before
    public void setup() {
        createDb("db/create-users-db.sql", "db/insert-user-with-addresses.sql");

        reader = new JdbcCursorItemReader<>();
        reader.setDataSource(db);
        reader.setRowMapper(new ColumnMapRowMapper());
    }

    @Test
    public void writeUserWithNestedAddresses() {
        reader.setSql(
                "SELECT users.*, addresses.street as \"address/street\", addresses.city as \"address/city\", addresses.zipCode as \"address/zipCode\" "
                        + "FROM users INNER JOIN addresses ON users.id = addresses.userId ORDER BY users.id");

        readAndWriteUsers();

        Fragment f = loadUserFromMarkLogic();
        f.assertElementValue("/user/ID", "1");
        f.assertElementValue("/user/NAME", "user1");
        f.assertElementExists("/user/address[1]/street[. = '123 Main St']");
        f.assertElementExists("/user/address[2]/street[. = '456 Main St']");
    }

    /**
     * When we don't assign a label with a "/" in it to each address column, then the address values will just be
     * flattened onto the user document. This is most likely not something anyone ever wants, but this test case is
     * included to show how it happens.
     */
    @Test
    public void writeUsersWithFlatAddresses() {
        reader.setSql("SELECT users.*, addresses.street, addresses.city, addresses.zipCode FROM users "
                + "INNER JOIN addresses ON users.id = addresses.userId ORDER BY users.id");

        readAndWriteUsers();

        Fragment f = loadUserFromMarkLogic();
        f.assertElementValue("/user/ID", "1");
        f.assertElementValue("/user/STREET[1]", "123 Main St");
        f.assertElementValue("/user/STREET[2]", "456 Main St");
        f.assertElementValue("/user/CITY[1]", "Falls Church");
        f.assertElementValue("/user/CITY[2]", "Arlington");
        f.assertElementValue("/user/ZIPCODE[1]", "22046");
        f.assertElementValue("/user/ZIPCODE[2]", "22207");
    }

    private void readAndWriteUsers() {
        ColumnMapItemWriter w = new ColumnMapItemWriter(getClient(), "user");
        PathAwareColumnMapProcessor p = new PathAwareColumnMapProcessor();
        launchJobWithStep(stepBuilderFactory.get("testStep").<Map<String, Object>, Map<String, Object>> chunk(1)
                .reader(reader).processor(p).writer(w).build());
    }

    private Fragment loadUserFromMarkLogic() {
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        String xml = mgr.read("/user/1.xml", new StringHandle()).get();
        return parse(xml);
    }
}
