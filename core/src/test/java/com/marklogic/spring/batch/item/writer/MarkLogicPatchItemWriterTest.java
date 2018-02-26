package com.marklogic.spring.batch.item.writer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
public class MarkLogicPatchItemWriterTest extends AbstractSpringBatchTest {

    DatabaseClient client;
    XMLDocumentManager docMgrXml;
    JSONDocumentManager docMgrJson;

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

        docMgrJson = client.newJSONDocumentManager();
        StringHandle text2 = new StringHandle("{ \"a\": {\n" +
                "    \"b\": \"value\",\n" +
                "    \"c1\": 1,\n" +
                "    \"c2\": 2,\n" +
                "    \"d\": null,\n" +
                "    \"e\": {\n" +
                "      \"f\": true,\n" +
                "      \"g\": [\"v1\", \"v2\", \"v3\"]\n" +
                "    }\n" +
                "} } ");
        docMgrJson.write("hello.json",text2);
    }


    @Test
    public void patchItemWriterTest() throws Exception {

        MarkLogicPatchItemWriter itemWriter = new MarkLogicPatchItemWriter(client);

        String[] infoJson = new String[2];
        infoJson[0] = "hello.json";
        infoJson[1] = "{ \"new\": \"content\"}";
        List<String[]> stringsJson = new ArrayList<String[]>();
        stringsJson.add(infoJson);
        itemWriter.write(stringsJson);
        StringHandle handleJson = docMgrJson.read("hello.json", new StringHandle());
        logger.info(handleJson.toString());


        String[] infoXml = new String[2];
        infoXml[0] = "hello.xml";
        infoXml[1] = "<new>content</new>";
        List<String[]> stringsXml = new ArrayList<String[]>();
        stringsXml.add(infoXml);
        itemWriter.write(stringsXml);
        StringHandle handleXml = docMgrXml.read("hello.xml", new StringHandle());
        logger.info(handleXml.toString());

        Fragment frag = new Fragment(handleXml.toString());

        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualObj = mapper.readTree(handleJson.toString());

        //assert Json
        assertThat(actualObj.findValuesAsText("new").get(0).toString(), equalTo("content"));

        //assert Xml
        frag.assertElementExists("Expecting new content", "//new[text() = 'content']");


    }
}
