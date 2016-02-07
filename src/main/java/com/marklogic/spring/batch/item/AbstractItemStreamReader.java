package com.marklogic.spring.batch.item;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import com.marklogic.client.helper.LoggingObject;

/**
 * Useful base class that stubs out the ItemStream methods (it's not often that all 3 need to be implemented) and
 * provides logging support.
 */
public abstract class AbstractItemStreamReader<T> extends LoggingObject implements ItemStreamReader<T> {

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

}
