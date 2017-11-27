package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.ext.batch.RestBatchWriter;
import com.marklogic.client.ext.batch.XccBatchWriter;
import com.marklogic.client.ext.xcc.DefaultDocumentWriteOperationAdapter;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.writer.support.DefaultUriTransformer;
import com.marklogic.spring.batch.item.writer.support.UriTransformer;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.template.XccTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * The MarkLogicItemWriter is an ItemWriter used to write any type of document to MarkLogic. It expects a list of
 * <a href="http://docs.marklogic.com/javadoc/client/com/marklogic/client/document/DocumentWriteOperation.html">DocumentWriteOperation</a>
 * instances, each of which encapsulates a write operation to MarkLogic.
 * <p>
 * It depends on an instance of BatchWriter from the ml-javaclient-util. This allows for either the REST API - a
 * DatabaseClient instance or set of instances - or XCC to be used for writing documents.
 * <p>
 * A UriTransformer can be optionally set to transform the URI of each incoming DocumentWriteOperation.
 */
public class MarkLogicItemWriter implements ItemWriter<DocumentWriteOperation>, ItemStream {

    private final static Logger logger = LoggerFactory.getLogger(MarkLogicItemWriter.class);

    protected UriTransformer uriTransformer;
    protected DatabaseClient client;
    protected DataMovementManager dataMovementManager;
    private int batchSize = 100;
    private int threadCount = 4;
    private ServerTransform serverTransform;
    private boolean isXcc = false;
    private boolean isDataMovementSdk = false;
    private boolean isRestApi = false;
    private boolean isWriteAsync = true;
    private Format contentFormat;

    //Used for XCC
    private XccBatchWriter xccBatchWriter;
    private List<ContentSource> contentSources;

    //Used for MarkLogic 9
    private WriteBatcher batcher;

    //Used for MarkLogic 8
    private RestBatchWriter batchWriter;

    public MarkLogicItemWriter(List<ContentSource> contentSources) {
        this.contentSources = contentSources;
        this.isXcc = true;
    }

    public MarkLogicItemWriter(DatabaseClient client) {
        this.client = client;
        String version = client.newServerEval().xquery("xdmp:version()").evalAs(String.class);
        logger.info("MarkLogic v" + version);
        if (version.startsWith("9")) {
            isDataMovementSdk = true;
        } else {
            isRestApi = true;
        }
        uriTransformer = new DefaultUriTransformer();
    }

    public MarkLogicItemWriter(DatabaseClient databaseClient, Format format) {
        this(databaseClient);
        this.contentFormat = format;
    }


    public MarkLogicItemWriter(DatabaseClient client, ServerTransform serverTransform) {
        this(client);
        this.serverTransform = serverTransform;
    }


    public MarkLogicItemWriter(DatabaseClient client, ServerTransform serverTransform, Format format) {
        this(client, serverTransform);
        this.contentFormat = format;
    }

    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        if (isXcc) {
            xccBatchWriter.initialize();
            xccBatchWriter.write(items);
            xccBatchWriter.waitForCompletion();
        } else if (isDataMovementSdk) {
            for (DocumentWriteOperation item : items) {
                batcher.add(uriTransformer.transform(item.getUri()), item.getMetadata(), item.getContent());
            }
        } else {
            List<DocumentWriteOperation> newItems = new ArrayList<>();
            for (DocumentWriteOperation op : items) {
                String newUri = uriTransformer.transform(op.getUri());
                newItems.add(
                        new DocumentWriteOperationImpl(
                                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                                newUri,
                                op.getMetadata(),
                                op.getContent()));
            }
            batchWriter.write(newItems);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (isDataMovementSdk) {
            dataMovementManager = client.newDataMovementManager();
            batcher = dataMovementManager.newWriteBatcher();
            batcher
                    .withBatchSize(getBatchSize())
                    .withThreadCount(getThreadCount());

            if (serverTransform != null) {
                batcher.withTransform(serverTransform);
            }
        } else if (isXcc){
            xccBatchWriter = new XccBatchWriter(contentSources);
        } else {
            batchWriter = new RestBatchWriter(client);
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
        if (isDataMovementSdk) {
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

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isWriteAsync() {
        return isWriteAsync;
    }

    public void setWriteAsync(boolean writeAsync) {
        isWriteAsync = writeAsync;
    }
}

