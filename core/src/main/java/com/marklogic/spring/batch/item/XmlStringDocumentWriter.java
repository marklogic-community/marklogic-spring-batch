package com.marklogic.spring.batch.item;

import java.util.List;
import java.util.UUID;

import com.marklogic.uri.UriGenerator;
import com.marklogic.uri.XmlStringUriGenerator;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;

/**
 * Generic writer for writing a list of strings, where each string is intended to be written as an XML document.
 */
public class XmlStringDocumentWriter extends AbstractDocumentWriter implements ItemWriter<String> {

    private XMLDocumentManager mgr;

    private XmlStringUriGenerator uriGenerator = new XmlStringUriGenerator();

    public XmlStringDocumentWriter(DatabaseClient client) {
        this.mgr = client.newXMLDocumentManager();
    }

    @Override
    public void write(List<? extends String> items) throws Exception {
        DocumentWriteSet set = mgr.newWriteSet();
        int size = items.size();
        logger.info("Building set of documents to write");
        for (int i = 0; i < size; i++) {
            String xml = items.get(i);
            String uri = uriGenerator.generateUri(xml, i + 1 + "");
            set.add(uri, buildMetadata(), new StringHandle(xml));
        }
        logger.info("Writing set of documents, size: " + size);
        mgr.write(set);
        logger.info("Finished writing set of documents");
    }

}
