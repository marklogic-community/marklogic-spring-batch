package com.marklogic.spring.batch.corb;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;

public class CorbReader<T> extends AbstractItemStreamItemReader<T> {

    private String urisModule;
    private DatabaseClient client;

    private EvalResultIterator resultIterator;

    public CorbReader(DatabaseClient client, String urisModule) {
        this.client = client;
        this.urisModule = urisModule;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.resultIterator = client.newServerEval().modulePath(urisModule).eval();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        T result = null;
        if (resultIterator.hasNext()) {
            EvalResult item = resultIterator.next();
            switch (item.getType()) {
            case INTEGER: {
                result = (T) item.getAs(Integer.class);
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
