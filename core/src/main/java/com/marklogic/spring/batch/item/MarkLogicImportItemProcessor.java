package com.marklogic.spring.batch.item;

import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

import java.io.File;

public class MarkLogicImportItemProcessor implements ItemProcessor<Resource, FileHandle> {

    public void setFormat(Format format) {
        this.format = format;
    }

    private Format format;

    public MarkLogicImportItemProcessor() {
        this.format = Format.UNKNOWN;
    }

    @Override
    public FileHandle process(Resource item) throws Exception {
        File file = item.getFile();
        FileHandle handle = new FileHandle(file);
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        String extension = (i >= 0) ? fileName.substring(i+1) : ".xyz";
        if (format.equals(Format.XML) || extension.equals("xml")) {
            handle.setFormat(Format.XML);
        } else if (format.equals(Format.JSON) || extension.equals("json")) {
            handle.setFormat(Format.JSON);
        } else if (format.equals(Format.TEXT) || extension.equals("txt")) {
            handle.setFormat(Format.TEXT);
        } else if (format.equals(Format.BINARY)) {
            handle.setFormat(Format.BINARY);
        } else {
            handle.setFormat(Format.UNKNOWN);
        }
        return handle;
    }
}
