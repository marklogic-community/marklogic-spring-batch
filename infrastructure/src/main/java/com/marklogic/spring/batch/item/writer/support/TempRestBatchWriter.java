package com.marklogic.spring.batch.item.writer.support;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.batch.RestBatchWriter;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.Format;

import java.util.List;

public class TempRestBatchWriter extends RestBatchWriter {

    public TempRestBatchWriter(DatabaseClient client) {
        super(client);
    }

    private Format contentFormat;

    public void setContentFormat(Format contentFormat) {
        this.contentFormat = contentFormat;
    }

    protected Runnable buildRunnable(final DatabaseClient client, final List<? extends DocumentWriteOperation> items) {
        return new Runnable() {
            @Override
            public void run() {
                DocumentManager<?, ?> mgr = buildDocumentManager(client);
                mgr.setContentFormat(contentFormat);
                DocumentWriteSet set = mgr.newWriteSet();
                for (DocumentWriteOperation item : items) {
                    set.add(item);
                }
                int count = set.size();
                if (logger.isDebugEnabled()) {
                    logger.debug("Writing " + count + " documents to MarkLogic");
                }
                if (getServerTransform() != null) {
                    mgr.write(set, getServerTransform());
                } else {
                    mgr.write(set);
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Wrote " + count + " documents to MarkLogic");
                }
            }
        };
    }
}
