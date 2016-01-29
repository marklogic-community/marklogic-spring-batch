package com.marklogic.client.spring.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.LoggingObject;

/**
 * Simple writer that expects a list of URIs, and then uses the Client API to delete those URIs.
 */
public class DeleteUriWriter extends LoggingObject implements ItemWriter<String> {

    private DatabaseClient client;

    public DeleteUriWriter(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public void write(List<? extends String> items) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Deleting URIs: " + items);
        }
        client.newDocumentManager().delete(items.toArray(new String[] {}));
    }

}
