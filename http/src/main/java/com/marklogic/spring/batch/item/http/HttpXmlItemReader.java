package com.marklogic.spring.batch.item.http;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.net.URI;
import java.util.Iterator;

public class HttpXmlItemReader implements ItemStreamReader<Document> {

    private URI uri;
    private String aggregateRecordElement;
    private RestTemplate restTemplate;
    private Iterator<Element> aggregateRecordElementItr;

    public HttpXmlItemReader(RestTemplate restTemplate, URI uri, String aggregateRecordElement) {
        this.restTemplate = restTemplate;
        this.uri = uri;
        this.aggregateRecordElement = aggregateRecordElement;
    }

    @Override
    public Document read() throws Exception {
        Document doc = null;
        if (aggregateRecordElementItr.hasNext()) {
            Element elem = aggregateRecordElementItr.next();
            doc = new Document(elem.clone());
        }
        return doc;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ItemStreamException(response.getStatusCode() + " HTTP response is returned");
        }
        ;
        MediaType type = response.getHeaders().getContentType();
        if (MediaType.APPLICATION_XML.equals(type) ||
            MediaType.APPLICATION_ATOM_XML.equals(type) ||
            MediaType.APPLICATION_XHTML_XML.equals(type) ||
            MediaType.APPLICATION_RSS_XML.equals(type) ||
            "text/xml".equals(type.toString())) {
            SAXBuilder jdomBuilder = new SAXBuilder();
            try {
                Document document = jdomBuilder.build(new StringReader(response.getBody()));
                ElementFilter ef = new ElementFilter(aggregateRecordElement);
                aggregateRecordElementItr = document.getRootElement().getDescendants(ef);
            } catch (Exception ex) {
                throw new ItemStreamException(ex);
            }
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
