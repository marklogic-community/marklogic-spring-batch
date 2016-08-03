package com.marklogic.spring.batch.item;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class MarkLogicItemWriter implements ItemWriter<DocumentWriteOperation> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private GenericDocumentManager docMgr;
    private DatabaseClient client;
    
    public MarkLogicItemWriter(DatabaseClient client) {
        this.client = client;
        docMgr = client.newDocumentManager();
    }
    
    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        DocumentWriteSet batch = docMgr.newWriteSet();
        for (DocumentWriteOperation item : items) {
            batch.add(item);
        }
        docMgr.write(batch);
    }
}
