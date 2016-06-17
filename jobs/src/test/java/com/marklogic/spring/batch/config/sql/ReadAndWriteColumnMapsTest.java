package com.marklogic.spring.batch.config.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.spring.batch.config.MigrateColumnMapsConfig;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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

    @Before
    public void setup() {
        createDb("db/create-users-db.sql", "db/insert-user-with-addresses.sql");
    }

    @Test
    public void writeUserWithNestedAddresses() {
        readAndWriteUsers("SELECT users.*, addresses.street as \"address/street\", addresses.city as \"address/city\", addresses.zipCode as \"address/zipCode\" "
                + "FROM users INNER JOIN addresses ON users.id = addresses.userId ORDER BY users.id");

        Fragment f = loadUserFromMarkLogic();
        f.assertElementValue("/user/ID", "1");
        f.assertElementValue("/user/NAME", "user1");
        f.assertElementExists("/user/address[1]/street[. = '123 Main St']");
        f.assertElementExists("/user/address[2]/street[. = '456 Main St']");
    }

    @Test
    public void writeUserWithNestedAddressesAsJson() throws Exception {
        String sql = "SELECT users.*, addresses.street as \"address/street\", addresses.city as \"address/city\", addresses.zipCode as \"address/zipCode\" "
                + "FROM users INNER JOIN addresses ON users.id = addresses.userId ORDER BY users.id";
        runJob(ReadAndWriteColumnMapsTestConfig.class, "--sql", sql, "--root_local_name", "user", "--format", "json");

        String content = getClient().newServerEval().xquery("collection('user')").evalAs(String.class);
        JsonNode node = new ObjectMapper().readTree(content);
        assertEquals(1, node.get("ID").asInt());
        assertEquals("user1", node.get("NAME").asText());
        assertEquals("123 Main St", node.get("address").get(0).get("street").asText());
        assertEquals("456 Main St", node.get("address").get(1).get("street").asText());
    }

    /**
     * When we don't assign a label with a "/" in it to each address column, then the address values will just be
     * flattened onto the user document. This is most likely not something anyone ever wants, but this test case is
     * included to show how it happens.
     */
    @Test
    public void writeUsersWithFlatAddresses() {
        readAndWriteUsers("SELECT users.*, addresses.street, addresses.city, addresses.zipCode FROM users "
                + "INNER JOIN addresses ON users.id = addresses.userId ORDER BY users.id");

        Fragment f = loadUserFromMarkLogic();
        f.assertElementValue("/user/ID", "1");
        f.assertElementValue("/user/STREET[1]", "123 Main St");
        f.assertElementValue("/user/STREET[2]", "456 Main St");
        f.assertElementValue("/user/CITY[1]", "Falls Church");
        f.assertElementValue("/user/CITY[2]", "Arlington");
        f.assertElementValue("/user/ZIPCODE[1]", "22046");
        f.assertElementValue("/user/ZIPCODE[2]", "22207");
    }

    private void readAndWriteUsers(String sql) {
        runJob(ReadAndWriteColumnMapsTestConfig.class, "--sql", sql, "--root_local_name", "user");
    }

    private Fragment loadUserFromMarkLogic() {
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        String xml = mgr.read("/user/1.xml", new StringHandle()).get();
        return parse(xml);
    }

    /**
     * With our embedded HSQL database, there's not a way that I know of for building a JDBC connection string for it.
     * So we override this method in the config class that we're testing to inject our own data source.
     */
    @Configuration
    public static class ReadAndWriteColumnMapsTestConfig extends MigrateColumnMapsConfig {
        @Override
        protected DataSource buildDataSource() {
            return embeddedDatabase;
        }
    }
}
