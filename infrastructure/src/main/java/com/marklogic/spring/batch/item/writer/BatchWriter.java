package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BatchWriter implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(BatchWriter.class);

    private DatabaseClient client;
    private List<? extends DocumentWriteOperation> items;
    private UriTransformer uriTransformer;
    private ServerTransform serverTransform;
    private Format returnFormat;


    public BatchWriter(DatabaseClient client, List<? extends DocumentWriteOperation> items, UriTransformer uriTransformer, ServerTransform serverTransform, Format returnFormat) {
        this.client = client;
        this.items = items;
        this.uriTransformer = uriTransformer;
        this.serverTransform = serverTransform;
        this.returnFormat = (returnFormat != null) ? returnFormat : Format.XML;
    }

    @Override
    public void run() {
        GenericDocumentManager mgr = client.newDocumentManager();
        mgr.setContentFormat(returnFormat);
        boolean transformOn = serverTransform != null ? true : false;

        DocumentWriteSet batch = mgr.newWriteSet();
        for (DocumentWriteOperation item : items) {
            batch.add(uriTransformer.transform(item.getUri()), item.getMetadata(), item.getContent());
        }
        int count = batch.size();
        if (logger.isDebugEnabled()) {
            logger.debug("Writing " + count + " documents to MarkLogic");
        }
        if (!transformOn) {
            mgr.write(batch);
        } else {
            mgr.write(batch, serverTransform);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Wrote " + count + " documents to MarkLogic");
        }
    }

    public void setUriTransformer(UriTransformer uriTransformer) {
        this.uriTransformer = uriTransformer;
    }
}