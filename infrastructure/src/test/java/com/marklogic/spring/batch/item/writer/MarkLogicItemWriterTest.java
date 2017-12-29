package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.document.*;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
public class MarkLogicItemWriterTest extends AbstractSpringBatchTest implements ApplicationContextAware {

    public String xml = "<hello>world</hello>";
    public String transformName = "simple";
    DatabaseClient client;
    DatabaseClient testDatabaseClient;
    XMLDocumentManager docMgr;

    @Autowired
    private DatabaseClientConfig batchDatabaseClientConfig;

    private ClientTestHelper clientTestHelper;
    private MarkLogicItemWriter itemWriter;

    public MarkLogicItemWriterTest() {
        super();
    }

    @Before
    public void setup() throws IOException {
        client = testDatabaseClient = DatabaseClientFactory.newClient(batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(), new DatabaseClientFactory.DigestAuthContext(batchDatabaseClientConfig.getUsername(),
                        batchDatabaseClientConfig.getPassword()));

        clientTestHelper = new ClientTestHelper();
        SimpleDatabaseClientProvider dbConfig = new SimpleDatabaseClientProvider(batchDatabaseClientConfig);
        clientTestHelper.setDatabaseClientProvider(dbConfig);

        testDatabaseClient = DatabaseClientFactory.newClient(batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(),
                new DatabaseClientFactory.DigestAuthContext(
                        batchDatabaseClientConfig.getUsername(), batchDatabaseClientConfig.getPassword()));
        docMgr = testDatabaseClient.newXMLDocumentManager();
        Resource transform = getApplicationContext().getResource("classpath:/transforms/simple.xqy");
        TransformExtensionsManager transMgr = testDatabaseClient.newServerConfigManager().newTransformExtensionsManager();
        FileHandle fileHandle = new FileHandle(transform.getFile());
        fileHandle.setFormat(Format.XML);
        transMgr.writeXQueryTransform(transformName, fileHandle);
    }

    public List<DocumentWriteOperation> getDocuments() {
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
    
    public List<DocumentWriteOperation> getBadDocument() {
        List<DocumentWriteOperation> handles = new ArrayList<DocumentWriteOperation>();
        
        DocumentWriteOperation handle2 = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "good.xml",
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello />"));
        handles.add(handle2);

        DocumentWriteOperation handle = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "fail.xml",
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello #&3; />"));
        handles.add(handle);

        return handles;
    }

    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        itemWriter.open(new ExecutionContext());
        itemWriter.write(items);
        itemWriter.close();
    }

    @Test
    public void writeTwoDocumentsTest() throws Exception {
        itemWriter = new MarkLogicItemWriter(client);
        itemWriter.setBatchSize(5);
        itemWriter.setThreadCount(2);
        write(getDocuments());
        clientTestHelper.assertInCollections("abc.xml", "raw");
        clientTestHelper.assertCollectionSize("Expecting two items in raw collection", "raw", 2);
    }
    
    class LongHolder {
    		public long writes = 0L;
    }
    
    @Test
    public void writeAndTrackTwoDocumentsTest() throws Exception {
    		final LongHolder lh = new LongHolder();
    		
        itemWriter = new MarkLogicItemWriter(client);
        itemWriter.setBatchSize(5);
        itemWriter.setThreadCount(2);
        itemWriter.setWriteBatchListener(batch -> {
        		lh.writes = batch.getJobWritesSoFar();
         });
        itemWriter.setWriteFailureListener((batch, throwable) -> {
    			ClientTestHelper.assertTrue("Should not get here, but will be set",false);
        });
        write(getDocuments());
        clientTestHelper.assertInCollections("abc.xml", "raw");
        clientTestHelper.assertCollectionSize("Expecting two items in raw collection", "raw", 2);
        ClientTestHelper.assertTrue("Batcher wrote 2 docs", lh.writes == 2L);
    }
    
    class StringHolder {
    		public String msg = null;
    }
    
    @Test
    public void failWriteDocumentsTest() throws Exception {
    		final StringHolder sh = new StringHolder();
    	
        itemWriter = new MarkLogicItemWriter(client);
        itemWriter.setBatchSize(5);
        itemWriter.setThreadCount(2);
        itemWriter.setWriteFailureListener((batch, throwable) -> {
        		sh.msg = throwable.getMessage();
        });
        itemWriter.setWriteBatchListener(batch -> {
        		ClientTestHelper.assertTrue("Should not get here, but will be set",false);
        });
        write(getBadDocument());
        clientTestHelper.assertCollectionSize("Expecting no items in raw collection", "raw", 0);
        ClientTestHelper.assertTrue("Exception expected bad character ", sh.msg.contains("XDMP-DOCSTARTTAGCHAR"));
    }

    @Test
    public void writeDocumentWithTransformNoParametersTest() {
        DocumentWriteOperation writeOp = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);

        try {
            itemWriter = new MarkLogicItemWriter(client, new ServerTransform(transformName));
            write(writeOps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringHandle handle = docMgr.read("hello.xml", new StringHandle());
        Fragment frag = new Fragment(handle.toString());
        frag.assertElementExists("//hello[text() = 'world']");
        frag.assertElementExists("//transform");
    }

    @Test
    public void writeDocumentWithTransformWithParametersTest() {
        DocumentWriteOperation writeOp = new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);
        try {
            ServerTransform serverTransform = new ServerTransform(transformName);
            serverTransform.addParameter("monster", "grover");
            serverTransform.addParameter("trash-can", "oscar");
            itemWriter = new MarkLogicItemWriter(client, serverTransform, Format.XML);
            write(writeOps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringHandle handle = docMgr.read("hello.xml", new StringHandle());
        Fragment frag = new Fragment(handle.toString());
        frag.assertElementExists("//transform");
        frag.assertElementExists("//monster[text() = 'grover']");
        frag.assertElementExists("//trash-can[text() = 'oscar']");
    }

    @Test
    public void writeBatchDocumentsWithTransformTest() {
        GenericDocumentManager docMgr = testDatabaseClient.newDocumentManager();
        docMgr.setContentFormat(Format.XML);
        DocumentWriteSet batch = docMgr.newWriteSet();
        batch.add("hello.xml", new DocumentMetadataHandle(), new StringHandle("<hello />"));
        batch.add("hello2.xml", new DocumentMetadataHandle(), new StringHandle("<hello2 />"));
        ServerTransform serverTransform = new ServerTransform("simple");
        itemWriter = new MarkLogicItemWriter(client, new ServerTransform(transformName));
        docMgr.write(batch, serverTransform);
    }

    @Test
    public void testTransformDocTest() {
        StringHandle handle = new StringHandle("<hello />");
        ServerTransform serverTransform = new ServerTransform(transformName);
        serverTransform.addParameter("monster", "grover");
        serverTransform.addParameter("trash-can", "oscar");
        GenericDocumentManager genDocMgr = testDatabaseClient.newDocumentManager();
        DocumentWriteSet writeSet = genDocMgr.newWriteSet();
        writeSet.add("aaa.xml", handle);

        //if this line is omitted then the test fails
        genDocMgr.setContentFormat(Format.XML);

        genDocMgr.write(writeSet, serverTransform);

    }

    @Test
    public void conflictingUpdateTest() throws Exception {
        List<DocumentWriteOperation> handles = getDocuments();

        //Add a document with a replica uri
        DocumentWriteOperation handle = new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                "abc.xml",
                new DocumentMetadataHandle().withCollections("raw"),
                new StringHandle("<hello />"));
        handles.add(handle);
        itemWriter = new MarkLogicItemWriter(client);
        write(handles);
        clientTestHelper.assertCollectionSize("Expecting zero items in raw collection", "raw", 0);
    }

    @Test(expected = NullPointerException.class)
    public void writeWithNullDataTest() throws Exception {
        itemWriter = new MarkLogicItemWriter(client);
        write(null);
    }

}
