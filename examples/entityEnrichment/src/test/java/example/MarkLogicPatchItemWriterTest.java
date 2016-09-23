package example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.junit.spring.BasicTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = { BasicConfig.class } )
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
    }
}
