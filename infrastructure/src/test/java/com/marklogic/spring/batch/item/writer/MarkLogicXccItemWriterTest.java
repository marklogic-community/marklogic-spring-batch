package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.batch.XccBatchWriter;
import com.marklogic.client.ext.xcc.DefaultDocumentWriteOperationAdapter;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import com.marklogic.xcc.*;
import com.marklogic.xcc.exceptions.XccConfigException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
@PropertySource(value = "classpath:job.properties")
public class MarkLogicXccItemWriterTest extends AbstractSpringBatchTest {

    MarkLogicItemWriter itemWriter;
    List<ContentSource> contentSources;

    @Autowired
    DatabaseClientConfig batchDatabaseClientConfig;

    @Value("${marklogic.database:msb}")
    private String databaseName;

    @Before
    public void setup() throws XccConfigException, URISyntaxException {
        contentSources = new ArrayList<ContentSource>();
        String connectionString = String.format("xcc://%s:%s@%s:%s/%s",
                batchDatabaseClientConfig.getUsername(),
                batchDatabaseClientConfig.getPassword(),
                batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(),
                databaseName);
        logger.info(connectionString);
        URI uri = new URI(connectionString);
        ContentSource contentSource = ContentSourceFactory.newContentSource(uri);
        contentSources.add(contentSource);
        itemWriter = new MarkLogicItemWriter(contentSources);
        itemWriter.open(new ExecutionContext());
    }

    @Test
    public void writeOneDocumentWithXccTest() throws Exception {
        List<DocumentWriteOperation> handles = new ArrayList<DocumentWriteOperation>();
        handles.add(getDocument());
        itemWriter.write(handles);

        assertEquals(1, numberOfRawDocuments());
    }

    @Test
    public void writeTwoDocumentsWithXccTest() throws Exception {
        List<DocumentWriteOperation> handles = new ArrayList<DocumentWriteOperation>();
        handles.add(getDocument());
        itemWriter.write(handles);

        handles = new ArrayList<DocumentWriteOperation>();
        handles.add(getDocument());
        itemWriter.write(handles);

        assertEquals(2, numberOfRawDocuments());
    }

    @Test
    public void writeThreeDocumentBatchWithXccTest() throws Exception {
        List<DocumentWriteOperation> handles = new ArrayList<DocumentWriteOperation>();
        handles.add(getDocument());
        handles.add(getDocument());
        handles.add(getDocument());
        itemWriter.write(handles);
        assertEquals(3, numberOfRawDocuments());

    }

    private int numberOfRawDocuments() throws Exception {
        Session session = contentSources.get(0).newSession();
        Request request = session.newAdhocQuery("xdmp:estimate(fn:collection('raw'))");
        ResultSequence rs = session.submitRequest(request);
        return Integer.parseInt(rs.asString());
    }


    private DocumentWriteOperation getDocument() {
        DocumentWriteOperation handle = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                UUID.randomUUID().toString(),
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello />"));

        return handle;
    }
}
