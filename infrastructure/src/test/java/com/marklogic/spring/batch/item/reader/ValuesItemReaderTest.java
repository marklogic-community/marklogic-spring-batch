package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class,
        com.marklogic.spring.batch.test.TestConfiguration.class
})
public class ValuesItemReaderTest extends AbstractSpringBatchTest {

    ClientTestHelper helper;
    DatabaseClient client;

    @Autowired
    DatabaseClientConfig batchDatabaseClientConfig;

    @Before
    public void setup() {
        DatabaseClientFactory.SecurityContext securityContext =
                new DatabaseClientFactory.DigestAuthContext(batchDatabaseClientConfig.getUsername(),
                        batchDatabaseClientConfig.getPassword());
        client = DatabaseClientFactory.newClient(batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(), securityContext);
        helper = new ClientTestHelper();
        helper.setDatabaseClientProvider(getClientProvider());

        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        StringHandle xml1 = new StringHandle("<hello />");
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("a");
    
        DocumentMetadataHandle metadata2 = new DocumentMetadataHandle();
        metadata2.withCollections("b");
        
        for (int i = 0; i < 600; i++) {
            DocumentMetadataHandle h = (i % 2 == 0) ? metadata : metadata2;
            docMgr.write("hello" + i + ".xml", h, xml1);
        }
        helper.assertCollectionSize("a = 300", "a", 300);
        helper.assertCollectionSize("b = 300", "b", 300);
    }
    
    @Test
    public void getUriValuesFromItemReaderTest() throws Exception {
        ValuesItemReader reader = new ValuesItemReader(client, getQueryOptions(), "uris");
        reader.open(new ExecutionContext());
        assertEquals("Expecting size of 600", reader.getLength(), 600);
        CountedDistinctValue val = reader.read();
        String uri = val.get("xs:string", String.class);
        logger.info(uri);
        assertTrue(uri.equals("hello0.xml"));
        val = reader.read();
        uri = val.get("xs:string", String.class);
        assertTrue(uri.equals("hello1.xml"));
        reader.close();
    }

    @Test
    public void getUriValuesWithQueryFromItemReaderTest() throws Exception {
        QueryDefinition qd = new StructuredQueryBuilder().collection("a");
        ValuesItemReader reader = new ValuesItemReader(client, getQueryOptions(), "uris", qd);
        reader.open(new ExecutionContext());
        assertEquals("Expecting size of 300", reader.getLength(), 300);
        CountedDistinctValue val;
        String uri;
        for (int i = 0; i < 600; i++) {
            if (i % 2 == 0) {
                val = reader.read();
                uri = val.get("xs:string", String.class);
                helper.assertInCollections(uri, "a");
            }

        }
        reader.close();
    }

    public StringHandle getQueryOptions() {
        return new StringHandle("<options xmlns=\"http://marklogic.com/appservices/search\">\n" +
                "    <search-option>unfiltered</search-option>\n" +
                "    <quality-weight>0</quality-weight>\n" +
                "    <values name=\"uris\">\n" +
                "        <uri/>\n" +
                "    </values>\n" +
                "</options>");
    }

    @Bean
    @Qualifier("batchXccTemplate")
    public XccTemplate xccTemplate(DatabaseClientConfig batchDatabaseClientConfig,
                                   @Value("${marklogic.database:Documents}") String databaseName) {
        return new XccTemplate(
                String.format("xcc://%s:%s@%s:8000/%s",
                        batchDatabaseClientConfig.getUsername(),
                        batchDatabaseClientConfig.getPassword(),
                        batchDatabaseClientConfig.getHost(),
                        databaseName));
    }

}
