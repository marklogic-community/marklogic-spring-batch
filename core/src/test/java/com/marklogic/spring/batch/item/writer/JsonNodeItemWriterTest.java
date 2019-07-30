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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.marklogic.client.document.DocumentPatchBuilder.Position.LAST_CHILD;
import static org.hamcrest.CoreMatchers.equalTo;

@ContextConfiguration(classes = {com.marklogic.spring.batch.config.MarkLogicConfiguration.class})
public class JsonNodeItemWriterTest extends AbstractSpringBatchTest {

    DatabaseClient client;

    JSONDocumentManager docMgrJson;

    String contextPath;

    @Autowired
    DatabaseClientConfig batchDatabaseClientConfig;

    @Before
    public void setup() {
        DatabaseClientFactory.SecurityContext securityContext =
                new DatabaseClientFactory.DigestAuthContext(
                        batchDatabaseClientConfig.getUsername(), batchDatabaseClientConfig.getPassword());
        client = DatabaseClientFactory.newClient(batchDatabaseClientConfig.getHost(),
                batchDatabaseClientConfig.getPort(), securityContext);

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

        contextPath = "a";
    }


    @Test
    public void patchJsonNodeItemWriterTest() throws Exception {

        JsonNodeItemWriter itemWriter = new JsonNodeItemWriter(client,contextPath,LAST_CHILD);

        String uri = "hello.json";
        String patchItem = "{ \"new\": \"content\"}";

        Map<String,String> infoJson = new HashMap<String, String>();

        infoJson.put(uri,patchItem) ;

        List<Map<String,String>> stringsJson = new ArrayList<Map<String, String>>();

        stringsJson.add(infoJson);

        itemWriter.write(stringsJson);

        StringHandle handleJson = docMgrJson.read("hello.json", new StringHandle());
        logger.info(handleJson.toString());


        ObjectMapper mapper = new ObjectMapper();

        JsonNode actualObj = mapper.readTree(handleJson.toString());

        //assert Json
        assertThat(actualObj.findValuesAsText("new").get(0).toString(), equalTo("content"));


    }
}
