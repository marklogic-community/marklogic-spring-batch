package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.item.processor.support.UriGenerator;
import org.springframework.core.io.Resource;

import java.util.UUID;

public class ResourceToDocumentWriteOperationItemProcessor extends AbstractMarkLogicItemProcessor<Resource> {

    public ResourceToDocumentWriteOperationItemProcessor() {
        super(new UriGenerator<Resource>(){
            @Override
            public String generateUri(Resource resource) {
                try {
                    return resource.getURL().getPath();
                } catch (Exception ex) {
                    return UUID.randomUUID().toString();
                }
            }
        });
    }

    @Override
    public AbstractWriteHandle getContentHandle(Resource item) throws Exception {
        String fileName = uriGenerator.generateUri(item);
        FileHandle handle = new FileHandle(item.getFile());
        int i = fileName.lastIndexOf('.');
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
