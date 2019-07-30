package com.marklogic.spring.batch.item.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonNodeItemWriter implements ItemWriter<Map<String,String>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    private String jsonContext;
    private DocumentPatchBuilder.Position contextPosition;

    //contextPath is the context path of the document which will be updated with the patch content. Position is the updating position of the contextPath
    public JsonNodeItemWriter(DatabaseClient client, String contextPath, DocumentPatchBuilder.Position contextPosition) {
        this.databaseClient = client;
        jsonContext = contextPath;
        this.contextPosition = contextPosition;
    }

    //A list of items. Each item is Map type. The key of Map is the uri of the document in the database. The value is the patch content.
    //The document is updating with the patch content
    @Override
    public void write(List<? extends Map<String,String>> items) throws Exception {

        for (Map<String,String> item : items) {

            String uri = item.keySet().toArray()[0].toString();
            String itemPatch = item.get(uri);
            logger.info("uri" + uri);
            logger.info("itemPatch" + itemPatch);

            JSONDocumentManager docJsonMgr = databaseClient.newJSONDocumentManager();

            DocumentPatchBuilder jsonPatchBldr = docJsonMgr.newPatchBuilder();

            Boolean isJson = isJSONValid(itemPatch);

            if (isJson)
            {
                 DocumentPatchHandle patchHandle = jsonPatchBldr.insertFragment(jsonContext, contextPosition, itemPatch).build();
                docJsonMgr.patch(uri, patchHandle);
            }


        }

    }


    public static boolean isJSONValid(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
