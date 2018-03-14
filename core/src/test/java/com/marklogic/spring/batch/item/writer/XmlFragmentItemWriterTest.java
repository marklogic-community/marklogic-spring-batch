package com.marklogic.spring.batch.item.writer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.io.StringHandle;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.marklogic.client.document.DocumentPatchBuilder.Position.LAST_CHILD;
import static org.hamcrest.CoreMatchers.equalTo;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
public class XmlFragmentItemWriterTest extends AbstractSpringBatchTest {

    DatabaseClient client;
    XMLDocumentManager docMgrXml;



    @Autowired
    DatabaseClientConfig batchDatabaseClientConfig;

    @Before
    public void setup() {
        DatabaseClientFactory.SecurityContext securityContext =
                new DatabaseClientFactory.DigestAuthContext(
                        batchDatabaseClientConfig.getUsername(), batchDatabaseClientConfig.getPassword());
        client = DatabaseClientFactory.newClient(batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(), securityContext);
        docMgrXml = client.newXMLDocumentManager();
        StringHandle text1 = new StringHandle("<doc><text>Abbey D'Agostino finished the race Tuesday after helping Nikki Hamblin of New Zealand back up and urging her to finish. The two clipped heels during the late part of the race and tumbled to the ground. Hamblin has indicated she will run in the final.  Emma Coburn, who took bronze in the women's 3,000 steeplechase, becoming the first American woman to medal in the event, reacted Wednesday</text></doc>");
        docMgrXml.write("hello.xml", text1);

    }


    @Test
    public void patchXmlFragmentItemWriteTest() throws Exception {

        XmlFragmentItemWriter itemWriter = new XmlFragmentItemWriter(client,"/doc",LAST_CHILD);

        String uri = "hello.xml";
        String patchItem = "<new>content</new>";

        Map<String,String> infoXml = new HashMap<String, String>();

        infoXml.put(uri,patchItem) ;

        List<Map<String,String>> stringsXml = new ArrayList<Map<String, String>>();

        stringsXml.add(infoXml);

        itemWriter.write(stringsXml);

        StringHandle handleXml = docMgrXml.read(stringsXml.get(0).keySet().toArray()[0].toString(), new StringHandle());

        logger.info(handleXml.toString());

        Fragment frag = new Fragment(handleXml.toString());

        ObjectMapper mapper = new ObjectMapper();


        //assert Xml
        frag.assertElementExists("Expecting new content", "//new[text() = 'content']");


    }
}
