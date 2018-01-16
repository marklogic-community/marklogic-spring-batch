package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.ext.batch.XccBatchWriter;
import com.marklogic.xcc.ContentSource;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class XccItemWriter implements ItemWriter<DocumentWriteOperation>, ItemStream {

    //Used for XCC
    private XccBatchWriter xccBatchWriter;
    private List<ContentSource> contentSources;

    public XccItemWriter(List<ContentSource> contentSources) {
        this.contentSources = contentSources;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        xccBatchWriter = new XccBatchWriter(contentSources);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }

    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        xccBatchWriter.initialize();
        xccBatchWriter.write(items);
        xccBatchWriter.waitForCompletion();
    }
}
