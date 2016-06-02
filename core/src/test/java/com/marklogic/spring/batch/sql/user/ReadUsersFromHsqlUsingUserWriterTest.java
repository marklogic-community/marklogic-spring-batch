package com.marklogic.spring.batch.sql.user;

import org.junit.Test;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.sql.AbstractHsqlTest;

/**
 * This test is an example of reading from a SQL database and writing to MarkLogic via a JDBC reader, a custom Java
 * class that maps to a table (the User class), and a RowMapper that maps a ResultSet to a User object. This pattern can
 * be followed for data migrations where it makes sense to create a lot of Java objects and row mappers to handle
 * different tables.
 */
public class ReadUsersFromHsqlUsingUserWriterTest extends AbstractHsqlTest {

    private final static int TABLE_ROW_CHUNK_SIZE = 10;

    private ItemReader<User> reader;
    private ItemWriter<User> writer;

    @Test
    public void withNoMetadata() {
        givenAnHsqlDatabaseWithSomeUsersInIt();
        givenAReaderToReadUsersFromTheHsqlDatabase();
        givenAnXmlWriterWithNoMetadata();
        whenTheJobIsRun();
        thenMarkLogicNowHasUserDocuments();
    }

    @Test
    public void withCollectionsAndPermissions() {
        givenAnHsqlDatabaseWithSomeUsersInIt();
        givenAReaderToReadUsersFromTheHsqlDatabase();
        givenAnXmlWriterWithCollectionsAndPermissions();
        whenTheJobIsRun();
        thenMarkLogicNowHasUserDocuments();
        thenMarkLogicNowHasUserDocumentsWithMetadata();
    }

    private void givenAnHsqlDatabaseWithSomeUsersInIt() {
        createDb("db/create-users-db.sql", "db/insert-hundred-users.sql");
    }

    private void givenAReaderToReadUsersFromTheHsqlDatabase() {
        JdbcCursorItemReader<User> r = new JdbcCursorItemReader<>();
        r.setDataSource(db);
        r.setSql(
                "SELECT users.*, comments.comment FROM users LEFT JOIN comments ON users.id = comments.userId ORDER BY users.id");
        r.setRowMapper(new UserRowMapper());
        this.reader = r;
    }

    private void givenAnXmlWriterWithNoMetadata() {
        this.writer = new UserWriter(getClient());
    }

    private void givenAnXmlWriterWithCollectionsAndPermissions() {
        UserWriter w = new UserWriter(getClient());
        w.setCollections("test1", "test2");
        w.setPermissions("temporal-admin,read,manage-admin,update");
        this.writer = w;
    }

    private void whenTheJobIsRun() {
        launchJobWithStep(stepBuilderFactory.get("testStep").<User, User> chunk(TABLE_ROW_CHUNK_SIZE)
                .reader(this.reader).writer(this.writer).build());
    }

    private void thenMarkLogicNowHasUserDocuments() {
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        assertTrue(mgr.read("/User/1.xml", new StringHandle()).get().contains(
                "<User><id>1</id><name>user1</name><email>user1@gmail.com</email><comments><comment>Hello from user1</comment><comment>Hello again from user1</comment></comments></User>"));
        assertTrue(mgr.read("/User/2.xml", new StringHandle()).get().contains(
                "<User><id>2</id><name>user2</name><email>user2@gmail.com</email><comments><comment>Hello from user2</comment></comments></User>"));
        assertTrue(mgr.read("/User/3.xml", new StringHandle()).get()
                .contains("<User><id>3</id><name>user3</name><email>user3@gmail.com</email></User>"));
    }

    private void thenMarkLogicNowHasUserDocumentsWithMetadata() {
        XMLDocumentManager mgr = getClient().newXMLDocumentManager();
        DocumentMetadataHandle h = mgr.readMetadata("/User/1.xml", new DocumentMetadataHandle());

        DocumentCollections colls = h.getCollections();
        assertTrue(colls.contains("test1"));
        assertTrue(colls.contains("test2"));

        DocumentPermissions perms = h.getPermissions();
        assertEquals(Capability.READ, perms.get("rest-reader").iterator().next());
        assertEquals(Capability.UPDATE, perms.get("rest-writer").iterator().next());
        assertEquals(Capability.READ, perms.get("temporal-admin").iterator().next());
        assertEquals(Capability.UPDATE, perms.get("manage-admin").iterator().next());
    }
}
