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

public class TempRestBatchWriter extends RestBatchWriter {

    private Format returnFormat;

    public TempRestBatchWriter(DatabaseClient databaseClient) {

        super(Arrays.asList(databaseClient));
        setReleaseDatabaseClients(false);
    }

    public TempRestBatchWriter(List<DatabaseClient> databaseClients) {

        super(databaseClients);
        setReleaseDatabaseClients(false);
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
                mgr.setContentFormat(returnFormat);
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
                if (logger.isDebugEnabled()) {
                    logger.debug("Wrote " + count + " documents to MarkLogic");
                }
            }
        });
    }

    public void setReturnFormat(Format returnFormat) {
        this.returnFormat = returnFormat;
    }
}
