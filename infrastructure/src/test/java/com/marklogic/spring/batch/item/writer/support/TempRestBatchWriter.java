package com.marklogic.spring.batch.item.writer.support;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.io.Format;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sstafford on 1/27/2017.
 */
public class TempRestBatchWriter extends RestBatchWriter {

    public TempRestBatchWriter(DatabaseClient databaseClient) {
        super(Arrays.asList(databaseClient));
    }


    public TempRestBatchWriter(List<DatabaseClient> databaseClients) {
        super(databaseClients);
    }

    @Override
    public void write(final List<? extends DocumentWriteOperation> items) {
        int clientIndex = getClientIndex();

        List<DatabaseClient> databaseClients = getDatabaseClients();
        ServerTransform serverTransform = getServerTransform();

        if (clientIndex >= databaseClients.size()) {
            clientIndex = 0;
        }
        final DatabaseClient client = databaseClients.get(clientIndex);
        clientIndex++;

        getTaskExecutor().execute(new Runnable() {
            @Override
            public void run() {
                DocumentManager<?, ?> mgr = buildDocumentManager(client);
                DocumentWriteSet set = mgr.newWriteSet();
                mgr.setContentFormat(Format.XML);
                for (DocumentWriteOperation item : items) {
                    set.add(item);
                }
                int count = set.size();
                if (logger.isDebugEnabled()) {
                    logger.debug("Writing " + count + " documents to MarkLogic");
                }
                if (serverTransform != null) {
                    mgr.write(set, serverTransform);
                } else {
                    mgr.write(set);
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Wrote " + count + " documents to MarkLogic");
                }
            }
        });
    }


}