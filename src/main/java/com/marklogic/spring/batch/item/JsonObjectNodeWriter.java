package com.marklogic.spring.batch.item;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.JacksonHandle;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by sanjuthomas on 5/24/16.
 */
public class JsonObjectNodeWriter implements ItemWriter<ObjectNode> {

    private static final String URI = "uri";

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    @Override
    public void write(List<? extends ObjectNode> items) throws Exception {
        DatabaseClient client = databaseClientProvider.getDatabaseClient();
        JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
        items.forEach(item -> {
            String uri = item.get(URI).textValue();
            item.remove(URI);
            jsonDocumentManager.write(uri, new JacksonHandle(item));
        });
    }
}
