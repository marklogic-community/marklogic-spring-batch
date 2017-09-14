package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.springframework.batch.item.*;

public class DocumentItemReader implements ItemStreamReader<DocumentRecord> {

    private DatabaseClientProvider databaseClientProvider;
    private GenericDocumentManager docMgr;
    private StructuredQueryDefinition queryDef;
    private DocumentPage page;
    private long numberOfPages = 0;
    private long start = 1L;

    public DocumentItemReader(DatabaseClientProvider databaseClientProvider, StructuredQueryDefinition queryDef) {
        this.databaseClientProvider = databaseClientProvider;
        this.queryDef = queryDef;
    }

    @Override
    public DocumentRecord read() throws Exception {
        if (page.hasNext()) {
            return page.next();
        } else if (page.hasNextPage()) {
            page = docMgr.search(queryDef, start);
            start += page.getPageSize();
            return page.next();
        } else {
            return null;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        docMgr = databaseClient.newDocumentManager();
        docMgr.setMetadataCategories(DocumentManager.Metadata.ALL);
        page = docMgr.search(queryDef, start);
        start += page.getPageSize();
        numberOfPages = page.getTotalPages();
        executionContext.put("PAGE_INDEX", page.getPageNumber());
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("PAGE_INDEX", page.getPageNumber());
    }

    @Override
    public void close() throws ItemStreamException {
        page.close();
    }
}

