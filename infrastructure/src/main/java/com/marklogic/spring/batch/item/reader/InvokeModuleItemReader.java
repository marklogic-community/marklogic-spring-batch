package com.marklogic.spring.batch.item.reader;

import java.net.URI;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.io.marker.JSONReadHandle;

public class InvokeModuleItemReader<T> implements ItemReader<T>, ItemStreamReader<T> {

    private String module;
    private DatabaseClient client;
    private EvalResultIterator resultIterator;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private long counter = 0;

    public InvokeModuleItemReader(DatabaseClient client, String module) {
        this.client = client;
        this.module = module;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.resultIterator = client.newServerEval().modulePath(module).eval();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        counter++;
        logger.info("Still processing: " + counter);
    }

    @Override
    public void close() throws ItemStreamException {
        resultIterator.close();
    }

    @Override
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
        } else {
            result = null;
        }
        return result;
    }

}
