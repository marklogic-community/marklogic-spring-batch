package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.UUID;

public class ResourceToDocumentWriteOperationItemProcessor extends AbstractMarkLogicItemProcessor<Resource> {

    public ResourceToDocumentWriteOperationItemProcessor() {
        super();
    }

    @Override
    public String getUri(Resource item) {
        try {
            return item.getURL().getPath();
        } catch (Exception ex) {
            return UUID.randomUUID().toString();
        }
    }

    @Override
    public AbstractWriteHandle getContentHandle(Resource item) throws Exception {
        File file = item.getFile();
        String fileName = getUri(item);
        FileHandle handle = new FileHandle(item.getFile());
        int i = getUri(item).lastIndexOf('.');
        String extension = (i >= 0) ? fileName.substring(i+1) : ".xyz";
        if (Format.XML.equals(getFormat()) || extension.equals("xml")) {
            handle.setFormat(Format.XML);
        } else if (Format.JSON.equals(getFormat()) || extension.equals("json")) {
            handle.setFormat(Format.JSON);
        } else if (Format.TEXT.equals(getFormat()) || extension.equals("txt")) {
            handle.setFormat(Format.TEXT);
        } else if (Format.BINARY.equals(getFormat())) {
            handle.setFormat(Format.BINARY);
        } else {
            handle.setFormat(Format.UNKNOWN);
        }
        return handle;
    }


}
