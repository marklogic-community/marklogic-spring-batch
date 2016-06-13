package com.marklogic.spring.batch.item;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by sanjuthomas on 5/24/16.
 */
public class JsonItemWriter implements ItemWriter<ObjectNode> {

    private DatabaseClient databaseClient;

    private UriGenerator uriGenerator = new DefaultUriGenerator();

    public JsonItemWriter(DatabaseClient databaseClient){
        this.databaseClient = databaseClient;
    }

    @Override
    public void write(List<? extends ObjectNode> items) throws Exception {
        JSONDocumentManager jsonDocumentManager = databaseClient.newJSONDocumentManager();
        items.forEach(item -> {
            jsonDocumentManager.write(uriGenerator.generate(), new JacksonHandle(item));
        });
    }
}
