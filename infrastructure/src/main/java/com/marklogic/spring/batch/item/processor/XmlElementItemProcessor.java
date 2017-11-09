package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.extra.jdom.JDOMHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.item.processor.support.UriGenerator;
import org.jdom2.Document;

public class XmlElementItemProcessor extends AbstractMarkLogicItemProcessor<Document> {

    public XmlElementItemProcessor() {
        super();
    }

    public XmlElementItemProcessor(UriGenerator<Document> uriGenerator) {
        super(uriGenerator);
    }

    @Override
    public AbstractWriteHandle getContentHandle(Document item) throws Exception {
        return new JDOMHandle(item);
    }

}
