package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.*;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.Format;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
The MarkLogicItemWriter is an ItemWriter used to write any type of document to MarkLogic.  It expects a
<a href="http://docs.marklogic.com/javadoc/client/com/marklogic/client/document/DocumentWriteOperation.html">DocumentWriteOperation</a> class.

@see <a href="">MarkLogicWriteHandle</a>
 */
public class MarkLogicItemWriter extends LoggingObject implements ItemWriter<DocumentWriteOperation>, ItemStream {

    private List<DatabaseClient> databaseClients;
    private int clientIndex = 0;

    private TaskExecutor taskExecutor;
    private List<Future<?>> futures = new ArrayList<>();
    private int threadCount = 16;

    private UriTransformer uriTransformer;
    private ServerTransform serverTransform;
    private Format returnFormat;
    
    public MarkLogicItemWriter(DatabaseClient client) {
        databaseClients = new ArrayList<>();
        databaseClients.add(client);
        //Add a default uri transformer
        uriTransformer = new UriTransformer(null, null, null);
    }

    public MarkLogicItemWriter(List<DatabaseClient> databaseClients) {
        this.databaseClients = databaseClients;
        uriTransformer = new UriTransformer(null, null, null);
    }
    
    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        DatabaseClient client = databaseClients.get(clientIndex);
        clientIndex++;
        if (clientIndex >= databaseClients.size()) {
            clientIndex = 0;
        }

        BatchWriter r = new BatchWriter(client, items, uriTransformer, serverTransform, returnFormat);

        if (taskExecutor instanceof AsyncTaskExecutor) {
            futures.add(((AsyncTaskExecutor) taskExecutor).submit(r));
        } else {
            taskExecutor.execute(r);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (threadCount > 1) {
            if (logger.isInfoEnabled()) {
                logger.info("Initializing thread pool with a count of " + threadCount);
            }
            ThreadPoolTaskExecutor tpte = new ThreadPoolTaskExecutor();
            tpte.setCorePoolSize(threadCount);
            tpte.afterPropertiesSet();
            this.taskExecutor = tpte;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Thread count is 1, so using a synchronous TaskExecutor");
            }
            this.taskExecutor = new SyncTaskExecutor();
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        int size = futures.size();
        if (logger.isDebugEnabled()) {
            logger.debug("Waiting for threads to finish document processing; futures count: " + size);
        }

        for (int i = 0; i < size; i++) {
            Future<?> f = futures.get(i);
            if (f.isDone()) {
                continue;
            }
            try {
                // Wait up to 1 hour for a write to ML to finish (should never happen)
                f.get(1, TimeUnit.HOURS);
            } catch (Exception ex) {
                logger.warn("Unable to wait for last set of documents to be processed: " + ex.getMessage(), ex);
            }
        }

        logger.info("Releasing DatabaseClient instances...");
        for (DatabaseClient client : databaseClients) {
            client.release();
        }
        logger.info("Finished writing data to MarkLogic!");
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setUriTransformer(UriTransformer uriTransformer) {
        this.uriTransformer = uriTransformer;
    }

    public void setServerTransform(ServerTransform serverTransform) {
        this.serverTransform = serverTransform;
    }

    public void setReturnFormat(Format returnFormat) {
        this.returnFormat = returnFormat;
    }
}

