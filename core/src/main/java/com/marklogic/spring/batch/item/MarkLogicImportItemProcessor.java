package com.marklogic.spring.batch.item;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

public class MarkLogicImportItemProcessor implements ItemProcessor<Resource, FileHandle> {

    private String documentType;

    public MarkLogicImportItemProcessor(String documentType) {
        this.documentType = documentType;
    }

    @Override
    public FileHandle process(Resource item) throws Exception {
        FileHandle handle = new FileHandle(item.getFile());
        if (documentType.toLowerCase().equals("xml")) {
            handle.setFormat(Format.XML);
        } else if (documentType.toLowerCase().equals("json")) {
            handle.setFormat(Format.JSON);
        } else if (documentType.toLowerCase().equals("text")) {
            handle.setFormat(Format.TEXT);
        } else if (documentType.toLowerCase().equals("binary")) {
            handle.setFormat(Format.BINARY);
        } else if (documentType.toLowerCase().equals("mixed")) {
            handle.setFormat(Format.UNKNOWN);
        } else {
            throw new Exception("Document Type " + documentType + " is unknown");
        }
        return handle;
    }
}
