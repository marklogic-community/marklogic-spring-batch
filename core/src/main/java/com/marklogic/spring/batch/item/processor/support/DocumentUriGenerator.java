package com.marklogic.spring.batch.item.processor.support;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class DocumentUriGenerator implements UriGenerator<Document> {

    private String elementId;
    private String namespace;

    public DocumentUriGenerator(String namespace, String elementId) {
        this.namespace = namespace;
        this.elementId = elementId;
    }

    public DocumentUriGenerator(String elementId) {
        this.elementId = elementId;
    }

    @Override
    public String generateUri(Document document) throws Exception {
        Element el = null;
        if (namespace == null) {
            el = document.getRootElement().getChild(elementId);
        } else {
            el = document.getRootElement().getChild(elementId, Namespace.getNamespace(namespace));
        }
        if (el == null) {
            throw new RuntimeException(namespace + ":" + elementId + " does not exist");
        } else if (el.getText().isEmpty()) {
            throw new RuntimeException(namespace + ":" + elementId + " is an empty string");
        } else {
            return el.getText();
        }
    }
}
