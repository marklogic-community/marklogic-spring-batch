package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.*;
import org.springframework.batch.item.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ValuesItemReader extends LoggingObject implements ItemReader<CountedDistinctValue>, ItemStream {

    private DatabaseClient databaseClient;
    private List<CountedDistinctValue> values;
    private QueryManager queryMgr;
    private ListIterator<CountedDistinctValue> itr;
    private int start;
    private String uriQuery;
    
    public int getLength() {
        return values.size();
    }
    
    public ValuesItemReader(DatabaseClient client) {
        this.databaseClient = client;
        start = 1;
        String uriQueryOptions =
                "<options xmlns=\"http://marklogic.com/appservices/search\">\n" +
                        "    <search-option>unfiltered</search-option>\n" +
                        "    <quality-weight>0</quality-weight>\n" +
                        "    <values name=\"uris\">\n" +
                        "        <uri/>\n" +
                        "    </values>\n" +
                        "</options>";
        QueryOptionsManager qoManager=
                databaseClient.newServerConfigManager().newQueryOptionsManager();
        qoManager.writeOptions("uris", new StringHandle(uriQueryOptions));
    }
    
    @Override
    public CountedDistinctValue read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return itr.hasNext() ? itr.next() : null;
    }
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        queryMgr = databaseClient.newQueryManager();
        
        ValuesDefinition vdef = queryMgr.newValuesDefinition("uris", "uris");
        StructuredQueryBuilder qb = new StructuredQueryBuilder();
        ValueQueryDefinition qDef = qb.collection("sourceXML");
        vdef.setQueryDefinition(qDef);
        
        ValuesHandle results = queryMgr.values(vdef, new ValuesHandle(), start);
        values = new ArrayList<CountedDistinctValue>(Arrays.asList(results.getValues()));
        itr = values.listIterator();
        return;
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        
    }
    
    @Override
    public void close() throws ItemStreamException {
        
    }
}
