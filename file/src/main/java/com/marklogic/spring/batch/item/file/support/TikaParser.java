package com.marklogic.spring.batch.item.file.support;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class TikaParser {

    public static String parseToXML(InputStream inputStream) throws IOException, SAXException, TikaException {
        ContentHandler handler = new ToXMLContentHandler();
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        parser.parse(inputStream, handler, metadata);
        return handler.toString();
    }
}
