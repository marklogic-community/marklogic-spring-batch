package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.io.marker.QueryOptionsWriteHandle;
import com.marklogic.client.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ValuesItemReader extends AbstractItemStreamItemReader<CountedDistinctValue> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String searchOptionsName;
    protected String valueName;
    protected QueryOptionsWriteHandle searchOptions;
    protected QueryDefinition queryDefinition;


    protected String TEMP_OPTIONS_NAME = "temp";
    private DatabaseClient databaseClient;
    private List<CountedDistinctValue> values;
    private ListIterator<CountedDistinctValue> itr;

    public ValuesItemReader(DatabaseClient client, String searchOptionsName, String valueName, QueryDefinition queryDef) {
        this.databaseClient = client;
        this.searchOptionsName = searchOptionsName;
        this.valueName = valueName;
        this.queryDefinition = queryDef;
    }

    public ValuesItemReader(DatabaseClient client, String searchOptionsName, String valueName) {
        this.databaseClient = client;
        this.searchOptionsName = searchOptionsName;
        this.valueName = valueName;
        this.queryDefinition = new StructuredQueryBuilder().and();
    }

    public ValuesItemReader(DatabaseClient client, QueryOptionsWriteHandle searchOptions, String valueName) {
        this(client, "temp", valueName);
        this.searchOptions = searchOptions;
        loadTempSearchOptions();
        this.queryDefinition = new StructuredQueryBuilder().and();
    }

    public ValuesItemReader(DatabaseClient client, QueryOptionsWriteHandle searchOptions, String valueName, QueryDefinition queryDef) {
        this(client, "temp", valueName);
        this.searchOptions = searchOptions;
        loadTempSearchOptions();
        this.queryDefinition = queryDef;
    }

    private void loadTempSearchOptions() {
        QueryOptionsManager qoManager=
                databaseClient.newServerConfigManager().newQueryOptionsManager();
        qoManager.writeOptions("temp", searchOptions);
    }

    
    @Override
    public CountedDistinctValue read() throws Exception {
        return itr.hasNext() ? itr.next() : null;
    }
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        QueryManager queryMgr = databaseClient.newQueryManager();
        
        ValuesDefinition vdef = queryMgr.newValuesDefinition(valueName, searchOptionsName);
        ValueQueryDefinition vqd = (ValueQueryDefinition) queryDefinition;
        vdef.setQueryDefinition(vqd);
        
        ValuesHandle results = queryMgr.values(vdef, new ValuesHandle(), 1);
        values = new ArrayList<CountedDistinctValue>(Arrays.asList(results.getValues()));
        itr = values.listIterator();
        return;
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        int index = itr.nextIndex();
        executionContext.put("iterator-index", itr.nextIndex());
        if (index % 250 == 0) {
            logger.info("#" + index + " of " + getLength() + ": " + values.get(index));
        }
    }
    
    @Override
    public void close() throws ItemStreamException {
        QueryOptionsManager qoManager=
                databaseClient.newServerConfigManager().newQueryOptionsManager();
        qoManager.deleteOptions(TEMP_OPTIONS_NAME);
    }

    public int getLength() {
        return values.size();
    }
}
