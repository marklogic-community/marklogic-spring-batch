package com.marklogic.spring.batch.samples.geonames;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.geonames.Geoname;
import org.springframework.batch.item.ItemProcessor;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GeonamesItemProcessor implements ItemProcessor<Geoname, DocumentWriteOperation> {

    protected JAXBContext jaxbContext() {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Geoname.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        return jaxbContext;
    }

    protected DocumentBuilder documentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        return docBuilderFactory.newDocumentBuilder();
    }

    @Override
    public DocumentWriteOperation process(Geoname item) throws Exception {
        Document doc = documentBuilder().newDocument();
        Marshaller marshaller = jaxbContext().createMarshaller();
        marshaller.marshal(item, doc);

        //Set document URI
        String uri = "http://geonames.org/geoname/" + item.getId();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections("geonames");
        return new DocumentWriteOperationImpl(DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                uri, metadata, new DOMHandle(doc));
    }

}
