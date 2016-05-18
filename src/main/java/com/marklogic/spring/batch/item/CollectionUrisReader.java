package com.marklogic.spring.batch.item;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.helper.LoggingObject;

/**
 * A reader for returning all the URIs in a collection.
 */
public class CollectionUrisReader extends LoggingObject implements ItemReader<String>, ItemStream {

    private DatabaseClient client;
    private EvalResultIterator resultIterator;
    private String[] collections;

    public CollectionUrisReader(DatabaseClient client, String... collections) {
        this.client = client;
        this.collections = collections;
    }

    @Override
    public String read() throws Exception {
        return resultIterator.hasNext() ? resultIterator.next().getAs(String.class) : null;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        String xquery = buildUrisQuery();
        logger.info("Reading URIs via query: " + xquery);
        resultIterator = client.newServerEval().xquery(xquery).eval();
    }

    protected String buildUrisQuery() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collections.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(format("'%s'", collections[i]));
        }
        return format("cts:uris((), (), cts:collection-query((%s)))", sb);
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() {
    }

}
