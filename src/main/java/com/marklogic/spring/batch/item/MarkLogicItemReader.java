package com.marklogic.spring.batch.item;

import java.net.URI;
import java.util.Date;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.io.marker.JSONReadHandle;

public class MarkLogicItemReader<T> extends AbstractItemStreamItemReader<T> {

    private String module;
    private DatabaseClient client;

    private EvalResultIterator resultIterator;

    public MarkLogicItemReader(DatabaseClient client, String module) {
        this.client = client;
        this.module = module;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.resultIterator = client.newServerEval().modulePath(module).eval();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public T read() throws Exception {
        T result = null;
        if (resultIterator.hasNext()) {
            EvalResult item = resultIterator.next();
            switch (item.getType()) {
            case ANYURI: {
            	result = (T) item.getAs(URI.class);
            	break;
            }
            case BASE64BINARY: {
            	break;
            }
            case BOOLEAN: {
            	result = (T) item.getAs(Boolean.class);
            	break;
            }
            case DATE: {
            	result = (T) item.getAs(Date.class);
            	break;
            }
            case DATETIME: {
            	result = (T) item.getAs(Date.class);
            	break;
            }
            case DECIMAL: {
            	result = (T) item.getAs(Float.class);
            	break;
            }
            case DOUBLE: {
            	result = (T) item.getAs(Double.class);
            	break;
            }
            case DURATION: {
            	result = (T) item.getAs(Integer.class);
            	break;
            }
            case FLOAT: {
            	result = (T) item.getAs(Float.class);
            	break;
            }
            case INTEGER: {
                result = (T) item.getAs(Integer.class);
                break;
            }
            case JSON: {
            	result = (T) item.getAs(JSONReadHandle.class);
            	break;
            }
            case NULL: {
            	result = null;
            	break;
            }
            case OTHER: {
            	result = (T) item.getAs(String.class);
            	break;
            }
            case STRING: {
                result = (T) item.getAs(String.class);
                break;
            }
            default: {
                break;
            }
            }
        }
        return result;
    }

}
