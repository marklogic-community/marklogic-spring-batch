package com.marklogic.spring.batch.item.reader;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.query.StructuredQueryDefinition;
import org.springframework.batch.item.*;

public class DocumentItemReader implements ItemStreamReader<DocumentRecord> {

    private DatabaseClientProvider databaseClientProvider;
    private GenericDocumentManager docMgr;
    private StructuredQueryDefinition queryDef;
    private DocumentPage page;

    public DocumentItemReader(DatabaseClientProvider databaseClientProvider, StructuredQueryDefinition queryDef) {
        this.databaseClientProvider = databaseClientProvider;
        this.queryDef = queryDef;
    }

    @Override
    public DocumentRecord read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (page.hasNext()) {
            return page.next();
        } else {
            return null;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        DatabaseClient databaseClient = databaseClientProvider.getDatabaseClient();
        docMgr = databaseClient.newDocumentManager();
        page = docMgr.search(queryDef, 1L);
        executionContext.put("PAGE_INDEX", page.getPageNumber());
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("PAGE_INDEX", page.getPageNumber());
    }

    @Override
    public void close() throws ItemStreamException {

    }
}
