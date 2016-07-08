package com.marklogic.spring.batch.item;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.uri.UriGenerator;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.UUID;

public class InputStreamHandleItemWriter extends AbstractDocumentWriter implements ItemWriter<InputStreamHandle>, UriGenerator<InputStreamHandle> {

    private DatabaseClient client;

    private GenericDocumentManager docMgr;

    public InputStreamHandleItemWriter(DatabaseClient client) {
        this.client = client;
        docMgr = client.newDocumentManager();
    }

    @Override
    public void write(List<? extends InputStreamHandle> items) throws Exception {
        DocumentWriteSet batch = docMgr.newWriteSet();
        for (InputStreamHandle item : items) {
            batch.add(generateUri(item, null), buildMetadata(), item);
        }
        docMgr.write(batch);
    }

    @Override
    public String generateUri(InputStreamHandle inputStreamHandle, String id) {
        String format = inputStreamHandle.getFormat().toString();
        String suffix = "";
        if (Format.XML.toString().equals(format)) {
            suffix = ".xml";
        } else if (Format.JSON.toString().equals(format)) {
            suffix = ".json";
        }
        return generate() + suffix;
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
