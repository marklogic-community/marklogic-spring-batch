package com.marklogic.spring.batch.item.writer;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.*;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.*;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { com.marklogic.spring.batch.config.MarkLogicApplicationContext.class })
public class MarkLogicItemWriterTest extends ClientTestHelper implements ApplicationContextAware {

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    private MarkLogicItemWriter itemWriter;
    List<DocumentWriteOperation> handles;
    public String xml;
    public String transformName = "simple";
    DatabaseClient databaseClient;
    TransformExtensionsManager transMgr;
    XMLDocumentManager docMgr;
    private ApplicationContext ctx;

    @Before
    public void setup() throws IOException {
        setDatabaseClientProvider(databaseClientProvider);
        databaseClient = databaseClientProvider.getDatabaseClient();
        itemWriter = new MarkLogicItemWriter(databaseClient);
        Resource transform = ctx.getResource("classpath:/transforms/simple.xqy");
        TransformExtensionsManager transMgr = databaseClient.newServerConfigManager().newTransformExtensionsManager();
        FileHandle fileHandle = new FileHandle(transform.getFile());
        fileHandle.setFormat(Format.XML);
        transMgr.writeXQueryTransform(transformName, fileHandle);
        xml = "<hello>world</hello>";
        docMgr = databaseClient.newXMLDocumentManager();


        handles = new ArrayList<DocumentWriteOperation>();

        MarkLogicWriteHandle handle = new MarkLogicWriteHandle();
        handle.setUri("abc.xml");
        handle.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle.setHandle(new StringHandle("<hello />"));
        handles.add(handle);

        MarkLogicWriteHandle handle2 = new MarkLogicWriteHandle();
        handle.setUri("abc.xml");
        handle.setMetadataHandle(new DocumentMetadataHandle().withCollections("raw"));
        handle.setHandle(new StringHandle("<hello2 />"));
        handles.add(handle2);
    }

    @Test
    public void writeTwoDocumentsTest() throws Exception {
        itemWriter.write(handles);
        assertInCollections("abc.xml", "raw");
        assertCollectionSize("Expecting two items in raw collection", "raw", 2);
    }

    @Test
    public void writeDocumentWithTransformNoParametersTest() {
        itemWriter.setTransform(Format.XML, transformName, null);
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
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("monster", "grover");
        parameters.put("trash-can", "oscar");
        itemWriter.setTransform(Format.XML, transformName, parameters);
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
        frag.assertElementExists("//transform");
        frag.assertElementExists("//monster[text() = 'grover']");
        frag.assertElementExists("//trash-can[text() = 'oscar']");
    }

    @Test
    public void writeBatchDocumentsWithTransformTest() {
        GenericDocumentManager docMgr = databaseClient.newDocumentManager();
        docMgr.setContentFormat(Format.XML);
        DocumentWriteSet batch = docMgr.newWriteSet();
        batch.add("/hello.xml", new DocumentMetadataHandle(), new StringHandle("<hello />"));
        batch.add("/hello2.xml", new DocumentMetadataHandle(), new StringHandle("<hello2 />"));
        ServerTransform serverTransform = new ServerTransform("simple");
        docMgr.write(batch, serverTransform);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
