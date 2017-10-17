package com.marklogic.spring.batch.item.writer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;

/**
 * Simple writer that expects a list of URIs, and then uses the Client API to delete those URIs.
 */
public class DeleteUriWriter implements ItemWriter<String> {

    private final static Logger logger = LoggerFactory.getLogger(DeleteUriWriter.class);

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
