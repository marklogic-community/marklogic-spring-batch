package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.TransformExtensionsManager;
import com.marklogic.client.document.*;
import com.marklogic.client.io.*;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = {BasicConfig.class})
public class MarkLogicItemWriterTest extends AbstractSpringTest {

    public String xml;
    public String transformName = "simple";
    DatabaseClient client;
    TransformExtensionsManager transMgr;
    XMLDocumentManager docMgr;

    @Before
    public void setup() throws IOException {
        Resource transform = getApplicationContext().getResource("classpath:/transforms/simple.xqy");
        client = getClientProvider().getDatabaseClient();
        TransformExtensionsManager transMgr = client.newServerConfigManager().newTransformExtensionsManager();
        FileHandle fileHandle = new FileHandle(transform.getFile());
        fileHandle.setFormat(Format.XML);
        transMgr.writeXQueryTransform(transformName, fileHandle);
        xml = "<hello>world</hello>";
        docMgr = client.newXMLDocumentManager();
    }

    @Test
    public void writeDocumentWithTransformNoParametersTest() {
        MarkLogicItemWriter writer = new MarkLogicItemWriter(client);
        writer.setTransform(Format.XML, transformName, null);
        DocumentWriteOperation writeOp = new MarkLogicWriteHandle("hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);
        try {
            writer.write(writeOps);
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
        MarkLogicItemWriter writer = new MarkLogicItemWriter(client);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("monster", "grover");
        parameters.put("trash-can", "oscar");
        writer.setTransform(Format.XML, transformName, parameters);
        DocumentWriteOperation writeOp = new MarkLogicWriteHandle("hello.xml", new DocumentMetadataHandle(), new StringHandle(xml));
        List<DocumentWriteOperation> writeOps = new ArrayList<DocumentWriteOperation>();
        writeOps.add(writeOp);
        try {
            writer.write(writeOps);
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
        GenericDocumentManager docMgr = client.newDocumentManager();
        docMgr.setContentFormat(Format.XML);
        DocumentWriteSet batch = docMgr.newWriteSet();
        batch.add("/hello.xml", new DocumentMetadataHandle(), new StringHandle("<hello />"));
        batch.add("/hello2.xml", new DocumentMetadataHandle(), new StringHandle("<hello2 />"));
        ServerTransform serverTransform = new ServerTransform("simple");
        docMgr.write(batch, serverTransform);
    }

    @After
    public void cleanup() {
//        transMgr.deleteTransform(transformName);
    }


}
