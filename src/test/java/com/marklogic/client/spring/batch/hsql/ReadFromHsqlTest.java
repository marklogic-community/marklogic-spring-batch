package com.marklogic.client.spring.batch.hsql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.spring.batch.AbstractSpringBatchTest;
import com.marklogic.client.spring.batch.TestConfig;

@ContextConfiguration(classes = { TestConfig.class })
public class ReadFromHsqlTest extends AbstractSpringBatchTest {

    private final static int TABLE_ROW_CHUNK_SIZE = 10;

    private EmbeddedDatabase db;
    private ItemReader<User> reader;
    private ItemWriter<User> writer;

    @Before
    public void setup() {
        givenAnHsqlDatabaseWithSomeUsersInIt();
    }

    @Test
    public void withNoMetadata() {
        givenAReaderToReadUsersFromTheHsqlDatabase();
        givenAnXmlWriterWithNoMetadata();
        whenTheJobIsRun();
        thenMarkLogicNowHasUserDocuments();
    }

    @Test
    public void withCollectionsAndPermissions() {
        givenAReaderToReadUsersFromTheHsqlDatabase();
        givenAnXmlWriterWithCollectionsAndPermissions();
        whenTheJobIsRun();
        thenMarkLogicNowHasUserDocuments();
        thenMarkLogicNowHasUserDocumentsWithMetadata();
    }

    @After
    public void teardown() {
        db.shutdown();
    }

    private void givenAnHsqlDatabaseWithSomeUsersInIt() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).addScript("db/create-db.sql").addScript("db/insert-data.sql")
                .build();
    }

    private void givenAReaderToReadUsersFromTheHsqlDatabase() {
        JdbcCursorItemReader<User> r = new JdbcCursorItemReader<>();
        r.setDataSource(db);
        r.setSql("SELECT users.*, comments.comment FROM users LEFT JOIN comments ON users.id = comments.userId ORDER BY users.id");
        r.setRowMapper(new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getInt(1));
                user.setName(rs.getString(2));
                user.setEmail(rs.getString(3));
                String comment = rs.getString(4);
                if (comment != null) {
                    user.getComments().add(comment);
                }
                return user;
            }
        });
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
