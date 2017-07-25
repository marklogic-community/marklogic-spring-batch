package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class MarkLogicPatchItemWriterTest extends AbstractSpringBatchTest {

    DatabaseClient client;
    XMLDocumentManager docMgr;

    @Autowired
    @Qualifier("batchDatabaseClientConfig")
    DatabaseClientConfig databaseClientConfig;
    
    @Before
    public void setup() {
        DatabaseClientFactory.SecurityContext securityContext = new DatabaseClientFactory.DigestAuthContext(databaseClientConfig.getUsername(), databaseClientConfig.getPassword());
        client = DatabaseClientFactory.newClient(databaseClientConfig.getHost(), databaseClientConfig.getPort(), securityContext);
        docMgr = client.newXMLDocumentManager();
        StringHandle text1 = new StringHandle("<doc><text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final.  Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday</text></doc>");
        docMgr.write("hello.xml", text1);
    }
    
    @Test
    public void patchItemWriterTest() throws Exception {
        MarkLogicPatchItemWriter itemWriter = new MarkLogicPatchItemWriter(client);
        String[] info = new String[2];
        info[0] = "hello.xml";
        info[1] = "<new>content</new>";
        List<String[]> strings = new ArrayList<String[]>();
        strings.add(info);
        itemWriter.write(strings);
        StringHandle handle = docMgr.read("hello.xml", new StringHandle());
        logger.info(handle.toString());
        Fragment frag = new Fragment(handle.toString());
        frag.assertElementExists("Expecting new content", "//new[text() = 'content']");
    }
}
