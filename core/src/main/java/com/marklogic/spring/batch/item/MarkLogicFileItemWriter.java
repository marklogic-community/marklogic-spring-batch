package com.marklogic.spring.batch.item;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Scope("step")
public class MarkLogicFileItemWriter extends AbstractDocumentWriter implements ItemWriter<FileHandle>, UriGenerator<File> {

    private DatabaseClient client;

    private GenericDocumentManager docMgr;

    private ExecutionContext executionContext;

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
        client.release();
    }

    @Override
    public void write(List<? extends FileHandle> items) throws Exception {
        DocumentWriteSet batch = docMgr.newWriteSet();
        for (FileHandle item : items) {
            batch.add(generateUri(item.get(), null), buildMetadata(), item);
        }
        docMgr.write(batch);
    }

    @Override
    public String generateUri(File file, String id) {
        String uri = file.toURI().toString();
        uri = (getOutputUriReplace() != null) ? applyOutputUriReplace(uri, getOutputUriReplace()) : uri;
        uri = (getOutputUriPrefix() != null) ? getOutputUriPrefix() + uri : uri;
        uri = (getOutputUriSuffix() != null) ? uri + getOutputUriSuffix() : uri;
        return uri;
    }

    public String applyOutputUriReplace(String uri, String outputUriReplace) {
        String[] regexReplace = outputUriReplace.split(",");
        for (int i = 0; i < regexReplace.length; i=i+2) {
            String regex = regexReplace[i];
            String replace = regexReplace[i+1];
            uri = uri.replaceAll(regex, replace);
        }
        return uri;
    }

    @Override
    public String generate() {
        return null;
    }
}
