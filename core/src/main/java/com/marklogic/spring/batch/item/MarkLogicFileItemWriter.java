package com.marklogic.spring.batch.item;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class MarkLogicFileItemWriter extends AbstractDocumentWriter implements ItemWriter<FileHandle> {

    private DatabaseClient client;

    private GenericDocumentManager docMgr;

    private ExecutionContext executionContext;

    public UriGenerator getUriGenerator() {
        return uriGenerator;
    }

    public void setUriGenerator(UriGenerator uriGenerator) {
        this.uriGenerator = uriGenerator;
    }

    private UriGenerator uriGenerator;

    public MarkLogicFileItemWriter(DatabaseClient databaseClient) {
        this.client = databaseClient;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        docMgr = client.newDocumentManager();
    }

    @Override
    public void close() {
    }

    @Override
    public void write(List<? extends FileHandle> items) throws Exception {
        DocumentWriteSet batch = docMgr.newWriteSet();
        for (FileHandle item : items) {
            batch.add(uriGenerator.generateUri(item.get().toURI().toString(), null), buildMetadata(), item);
        }
        docMgr.write(batch);
    }

}
