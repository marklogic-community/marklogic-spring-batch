package com.marklogic.spring.batch.item.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Simple reader for reading all the files in a directory. Includes optional support for a
 * FileFilter or FilenameFilter.
 */
public class DirectoryReader extends AbstractItemStreamItemReader<File> {

    private File dir;
    private File[] files;
    private int counter = 0;
    private FileFilter fileFilter;
    private FilenameFilter filenameFilter;

    public DirectoryReader(File dir) {
        this.dir = dir;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (fileFilter != null) {
            files = dir.listFiles(fileFilter);
        }
        else if (filenameFilter != null) {
            files = dir.listFiles(filenameFilter);
        }
        else {
            files = dir.listFiles();
        }
    }

    @Override
    public File read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (files.length > counter) {
            return files[counter++];
        }
        return null;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public void setFilenameFilter(FilenameFilter filenameFilter) {
        this.filenameFilter = filenameFilter;
    }

}
