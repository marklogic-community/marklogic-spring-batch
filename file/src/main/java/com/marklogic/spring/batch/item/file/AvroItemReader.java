package com.marklogic.spring.batch.item.file;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.IOException;

public class AvroItemReader implements ItemReader {

    protected final static Logger logger = LoggerFactory.getLogger(AvroItemReader.class);

    private GenericRecord record;
    private DataFileReader<GenericRecord> dataFileReader;

    public AvroItemReader(File inputFile) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        this.dataFileReader = new DataFileReader<GenericRecord>(inputFile, datumReader);

        this.record = null;
    }

    @Override
    public synchronized String read() throws Exception {

        if (this.dataFileReader.hasNext()) {
            this.record = this.dataFileReader.next(this.record);

            logger.info(record.toString());

            return this.record.toString();
        } else {
            return null;
        }

    }

}
