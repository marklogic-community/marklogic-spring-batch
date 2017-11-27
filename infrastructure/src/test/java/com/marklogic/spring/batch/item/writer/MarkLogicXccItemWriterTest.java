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
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = {com.marklogic.spring.batch.test.TestConfiguration.class})
@PropertySource(value = "classpath:job.properties")
public class MarkLogicXccItemWriterTest extends AbstractSpringBatchTest {

    MarkLogicItemWriter itemWriter;
    List<ContentSource> contentSources;

    @Autowired
    DatabaseClientConfig batchDatabaseClientConfig;

    @Before
    public void setup() throws XccConfigException, URISyntaxException {
        contentSources = new ArrayList<ContentSource>();
        String connectionString = String.format("xcc://%s:%s@%s:%s/%s",
                batchDatabaseClientConfig.getUsername(),
                batchDatabaseClientConfig.getPassword(),
                batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(),
                "marklogic-spring-batch-test-content");
        logger.info(connectionString);
        URI uri = new URI(connectionString);
        ContentSource contentSource = ContentSourceFactory.newContentSource(uri);
        contentSources.add(contentSource);
        itemWriter = new MarkLogicItemWriter(contentSources);
        itemWriter.open(new ExecutionContext());
    }

    @Test
    public void writeSingleDocumentViaXccTest() throws Exception {
        itemWriter.write(getDocuments());
    }

    @Test
    public void writeSingleDocTest() throws Exception {
        XccBatchWriter xccBatchWriter = new XccBatchWriter(contentSources);
        xccBatchWriter.initialize();
        xccBatchWriter.setDocumentWriteOperationAdapter(new DefaultDocumentWriteOperationAdapter());
        xccBatchWriter.write(getDocuments());
        xccBatchWriter.waitForCompletion();
    }

    private List<DocumentWriteOperation> getDocuments() {
        List<DocumentWriteOperation> handles = new ArrayList<DocumentWriteOperation>();

        DocumentWriteOperation handle = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "abc.xml",
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello />"));
        handles.add(handle);

        DocumentWriteOperation handle2 = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "abc2.xml",
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello2 />"));
        handles.add(handle2);

        return handles;
    }
}
