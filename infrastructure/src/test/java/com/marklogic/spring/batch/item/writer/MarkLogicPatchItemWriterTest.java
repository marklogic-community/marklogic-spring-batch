package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = { com.marklogic.spring.batch.config.MarkLogicApplicationContext.class })
public class MarkLogicPatchItemWriterTest extends AbstractSpringTest {
    
    XMLDocumentManager docMgr;
    
    @Before
    public void setup() {
        DatabaseClient client = getClient();
        docMgr = client.newXMLDocumentManager();
        StringHandle text1 = new StringHandle("<doc><text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final.  Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday</text></doc>");
        docMgr.write("hello.xml", text1);
    }
    
    @Test
    public void patchItemWriterTest() throws Exception {
        MarkLogicPatchItemWriter itemWriter = new MarkLogicPatchItemWriter(getClient());
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
