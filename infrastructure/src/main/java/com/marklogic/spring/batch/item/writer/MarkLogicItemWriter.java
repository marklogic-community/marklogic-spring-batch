package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.BatchWriter;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.*;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    private UriTransformer uriTransformer;
    private BatchWriter batchWriter;

    public MarkLogicItemWriter(DatabaseClient client) {
        this(Arrays.asList(client));
    }

    public MarkLogicItemWriter(List<DatabaseClient> databaseClients) {
        this(new RestBatchWriter(databaseClients));
    }

    public MarkLogicItemWriter(BatchWriter batchWriter) {
        this.batchWriter = batchWriter;
    }

    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
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

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (logger.isInfoEnabled()) {
            logger.info("On stream open, initializing BatchWriter");
        }
        batchWriter.initialize();
        if (logger.isInfoEnabled()) {
            logger.info("On stream open, finished initializing BatchWriter");
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        if (logger.isInfoEnabled()) {
            logger.info("On stream close, waiting for BatchWriter to complete");
        }
        batchWriter.waitForCompletion();
        if (logger.isInfoEnabled()) {
            logger.info("On stream close, finished waiting for BatchWriter to complete");
        }
    }

    public void setUriTransformer(UriTransformer uriTransformer) {
        this.uriTransformer = uriTransformer;
    }
}

