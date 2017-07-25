package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.*;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.writer.support.TempRestBatchWriter;
import org.springframework.batch.item.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The MarkLogicItemWriter is an ItemWriter used to write any type of document to MarkLogic. It expects a list of
 * <a href="http://docs.marklogic.com/javadoc/client/com/marklogic/client/document/DocumentWriteOperation.html">DocumentWriteOperation</a>
 * instances, each of which encapsulates a write operation to MarkLogic.
 *
 * It depends on an instance of BatchWriter from the ml-javaclient-util. This allows for either the REST API - a
 * DatabaseClient instance or set of instances - or XCC to be used for writing documents.
 *
 * A UriTransformer can be optionally set to transform the URI of each incoming DocumentWriteOperation.
 */
public class MarkLogicItemWriter extends LoggingObject implements ItemWriter<DocumentWriteOperation>, ItemStream {

    protected UriTransformer uriTransformer;
    protected DatabaseClient client;
    protected DataMovementManager dataMovementManager;
    private int BATCH_SIZE = 100;
    private int THREAD_COUNT = 4;
    private ServerTransform serverTransform;

    private Format contentFormat;

    //Used for MarkLogic 9
    private WriteBatcher batcher;

    //Used for MarkLogic 8
    private TempRestBatchWriter batchWriter;

    private boolean marklogicVersion9 = true;

    public MarkLogicItemWriter(DatabaseClient client) {
        this.client = client;
        String version = client.newServerEval().xquery("xdmp:version()").evalAs(String.class);
        logger.info("MarkLogic v" + version);
        if (!version.startsWith("9")) {
            marklogicVersion9 = false;
        }
    }

    public MarkLogicItemWriter(DatabaseClient client, UriTransformer uriTransformer) {
        this(client);
        this.uriTransformer = uriTransformer;
    }

    public MarkLogicItemWriter(DatabaseClient client, ServerTransform serverTransform) {
        this(client);
        this.serverTransform = serverTransform;
    }

    public MarkLogicItemWriter(DatabaseClient client, UriTransformer uriTransformer, ServerTransform serverTransform) {
        this(client, uriTransformer);
        this.serverTransform = serverTransform;
    }

    public MarkLogicItemWriter(DatabaseClient client, ServerTransform serverTransform, Format format) {
        this(client, serverTransform);
        this.contentFormat = format;
    }

    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        if (marklogicVersion9) {
            for (DocumentWriteOperation item : items) {
                if (uriTransformer != null) {
                    String newUri = uriTransformer.transform(item.getUri());
                    batcher.add(newUri, item.getMetadata(), item.getContent());
                } else {
                    batcher.add(item.getUri(), item.getMetadata(), item.getContent());
                }
            }
        } else {
            if (uriTransformer != null) {
                List<DocumentWriteOperation> newItems = new ArrayList<>();
                for (DocumentWriteOperation op : items) {
                    String newUri = uriTransformer.transform(op.getUri());
                    newItems.add(new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                            newUri, op.getMetadata(), op.getContent()));
                }
                batchWriter.write(newItems);
            } else {
                batchWriter.write(items);
            }
        }

    }

    public int getBatchSize() {
        return BATCH_SIZE;
    }

    public int getThreadCount() {
        return THREAD_COUNT;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (marklogicVersion9) {
            dataMovementManager = client.newDataMovementManager();
            batcher = dataMovementManager.newWriteBatcher();
            batcher
              .withBatchSize(getBatchSize())
              .withThreadCount(getThreadCount());

            if (serverTransform != null) {
                batcher.withTransform(serverTransform);
            }
        } else {
            batchWriter = new TempRestBatchWriter(client);
            if (serverTransform != null) {
                batchWriter.setServerTransform(serverTransform);
                batchWriter.setContentFormat(contentFormat == null ? Format.XML : contentFormat);
            }
            batchWriter.initialize();
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        if (marklogicVersion9) {
            batcher.flushAndWait();
            dataMovementManager.release();
        } else {
            batchWriter.waitForCompletion();
        }
        //client.release();

    }

    public void setServerTransform(ServerTransform serverTransform) {
        this.serverTransform = serverTransform;
    }

    public void setUriTransformer(UriTransformer uriTransformer) {
        this.uriTransformer = uriTransformer;
    }

    public void setContentFormat(Format format) {
        this.contentFormat = format;
    }
}

