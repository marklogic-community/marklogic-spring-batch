package com.marklogic.spring.batch.config.sql;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentPermissions;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.config.sql.user.User;
import com.marklogic.spring.batch.config.sql.user.UserRowMapper;
import com.marklogic.spring.batch.config.sql.user.UserWriter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.xml.crypto.Data;

/**
 * This test is an example of reading from a SQL database and writing to MarkLogic via a JDBC reader, a custom Java
 * class that maps to a table (the User class), and a RowMapper that maps a ResultSet to a User object. This pattern can
 * be followed for data migrations where it makes sense to create a lot of Java objects and row mappers to handle
 * different tables.
 */
public class ReadUsersFromHsqlUsingUserWriterTest extends AbstractHsqlTest {

    @Before
    public void setup() {
        createDb("db/create-users-db.sql", "db/insert-hundred-users.sql");
    }

    @Test
    public void withNoMetadata() {
        runJob(ReadUsersFromHsqlConfig.class);
        thenMarkLogicNowHasUserDocuments();
    }

    @Test
    public void withCollectionsAndPermissions() {
        runJob(ReadUsersFromHsqlConfig.class, "--collections", "test1,test2", "--permissions", "temporal-admin,read,manage-admin,update");
        thenMarkLogicNowHasUserDocuments();
        thenMarkLogicNowHasUserDocumentsWithMetadata();
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

    @EnableBatchProcessing
    public static class ReadUsersFromHsqlConfig {

        @Bean
        public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step1) {
            return jobBuilderFactory.get("readUsersJob").start(step1).build();
        }

        @Bean
        @JobScope
        protected Step step1(
                DatabaseClientProvider databaseClientProvider,
                StepBuilderFactory stepBuilderFactory,
                @Value("#{jobParameters['collections']}") String[] collections,
                @Value("#{jobParameters['permissions']}") String permissions) {

            JdbcCursorItemReader<User> r = new JdbcCursorItemReader<>();
            r.setDataSource(embeddedDatabase);
            r.setSql("SELECT users.*, comments.comment FROM users LEFT JOIN comments ON users.id = comments.userId ORDER BY users.id");
            r.setRowMapper(new UserRowMapper());

            UserWriter w = new UserWriter(databaseClientProvider.getDatabaseClient());
            w.setCollections(collections);
            w.setPermissions(permissions);

            return stepBuilderFactory.get("step1")
                    .<User, User>chunk(10)
                    .reader(r)
                    .writer(w)
                    .build();
        }
    }
}
