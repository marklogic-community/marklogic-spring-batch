package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.*;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.*;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { com.marklogic.spring.batch.config.MarkLogicApplicationContext.class })
public class MarkLogicItemWriterTest extends AbstractSpringTest implements ApplicationContextAware {

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    private MarkLogicItemWriter itemWriter;
    private RestBatchWriter restBatchWriter;
    List<DocumentWriteOperation> handles;
    public String xml = "<hello>world</hello>";
    public String transformName = "simple";
    DatabaseClient databaseClient;
    TransformExtensionsManager transMgr;
    XMLDocumentManager docMgr;
    private ApplicationContext ctx;
    private ClientTestHelper clientTestHelper;

    @Before
    public void setup() throws IOException {
        clientTestHelper = new ClientTestHelper();
        clientTestHelper.setDatabaseClientProvider(databaseClientProvider);
        databaseClient = databaseClientProvider.getDatabaseClient();

        restBatchWriter = new RestBatchWriter(Arrays.asList(databaseClient));

        itemWriter = new MarkLogicItemWriter(restBatchWriter);
        itemWriter.open(new ExecutionContext());

        Resource transform = ctx.getResource("classpath:/transforms/simple.xqy");
        TransformExtensionsManager transMgr = databaseClient.newServerConfigManager().newTransformExtensionsManager();
        FileHandle fileHandle = new FileHandle(transform.getFile());
        fileHandle.setFormat(Format.XML);
        transMgr.writeXQueryTransform(transformName, fileHandle);

        handles = new ArrayList<DocumentWriteOperation>();

        MarkLogicWriteHandle handle = new MarkLogicWriteHandle();
        handle.setUri("abc.xml");
        handle.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle.setHandle(new StringHandle("<hello />"));
        handles.add(handle);

        MarkLogicWriteHandle handle2 = new MarkLogicWriteHandle();
        handle2.setUri("abc2.xml");
        handle2.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle2.setHandle(new StringHandle("<hello2 />"));
        handles.add(handle2);

        docMgr = databaseClient.newXMLDocumentManager();
    }
    
    @Test
    public void writeTwoDocumentsTest() throws Exception {
        itemWriter.write(handles);
        clientTestHelper.assertInCollections("abc.xml", "raw");
        clientTestHelper.assertCollectionSize("Expecting two items in raw collection", "raw", 2);
    }

    @Test
    public void writeDocumentWithTransformNoParametersTest() {
        ServerTransform transform = new ServerTransform(transformName);
        restBatchWriter.setServerTransform(transform);
        //itemWriter.setReturnFormat(Format.XML);

        DocumentWriteOperation writeOp = new MarkLogicWriteHandle("hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);

        try {
            itemWriter.write(writeOps);
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
        ServerTransform serverTransform = new ServerTransform(transformName);
        serverTransform.addParameter("monster", "grover");
        serverTransform.addParameter("trash-can", "oscar");
        restBatchWriter.setServerTransform(serverTransform);
        //itemWriter.setReturnFormat(Format.XML);
        DocumentWriteOperation writeOp = new MarkLogicWriteHandle("hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);
        try {
            itemWriter.write(writeOps);
            itemWriter.close();
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
        GenericDocumentManager docMgr = databaseClient.newDocumentManager();
        docMgr.setContentFormat(Format.XML);
        DocumentWriteSet batch = docMgr.newWriteSet();
        batch.add("hello.xml", new DocumentMetadataHandle(), new StringHandle("<hello />"));
        batch.add("hello2.xml", new DocumentMetadataHandle(), new StringHandle("<hello2 />"));
        ServerTransform serverTransform = new ServerTransform("simple");
        docMgr.write(batch, serverTransform);
    }

    @Test
    public void testTransformDocTest() {
        StringHandle handle = new StringHandle("<hello />");
        ServerTransform serverTransform = new ServerTransform(transformName);
        serverTransform.addParameter("monster", "grover");
        serverTransform.addParameter("trash-can", "oscar");
        GenericDocumentManager genDocMgr = databaseClient.newDocumentManager();
        DocumentWriteSet writeSet = genDocMgr.newWriteSet();
        writeSet.add("aaa.xml", handle);

        //if this line is omitted then the test fails
        genDocMgr.setContentFormat(Format.XML);

        genDocMgr.write(writeSet, serverTransform);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
