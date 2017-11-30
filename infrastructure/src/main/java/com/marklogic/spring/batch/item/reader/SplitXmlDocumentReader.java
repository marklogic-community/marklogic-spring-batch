package com.marklogic.spring.batch.item.reader;

import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;


public class SplitXmlDocumentReader extends AbstractItemStreamItemReader<Document> {

    private String filePath;
    private String aggregateRecordElement;
    private String aggregateUriId;
    private String aggregateRecordNamespace;
    private Resource xmlResource;
    private NodeList nodeList;
    private int cursor;
    private DocumentBuilder documentBuilder;

    public SplitXmlDocumentReader() {
        super();
    }

    public SplitXmlDocumentReader(String filePath, String aggregateRecordElement) {
        this.filePath = filePath;
        this.aggregateRecordElement = aggregateRecordElement;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            xmlResource = resolver.getResource(filePath);
            XPath xPath = XPathFactory.newInstance().newXPath();

            Document doc = documentBuilder.parse(xmlResource.getFile());
            nodeList = (NodeList)xPath.evaluate("//" + aggregateRecordElement,
                    doc.getDocumentElement(), XPathConstants.NODESET);
            cursor = 0;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ItemStreamException(e);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new ItemStreamException(e);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new ItemStreamException(e);
        }

    }


    @Override
    public Document read() throws Exception {
        if (cursor < nodeList.getLength()) {
            Node node = nodeList.item(cursor);
            Document newDocument = documentBuilder.newDocument();
            Node importedNode = newDocument.importNode(node, true);
            newDocument.appendChild(importedNode);
            newDocument.setDocumentURI(aggregateRecordElement + "-" + cursor + ".xml");
            cursor++;
            return newDocument;
        } else return null;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setAggregateRecordElement(String aggregateRecordElement) {
        this.aggregateRecordElement = aggregateRecordElement;
    }

    public void setAggregateUriId(String aggregateUriId) {
        this.aggregateUriId = aggregateUriId;
    }

    public void setAggregateRecordNamespace(String aggregateRecordNamespace) {
        this.aggregateRecordNamespace = aggregateRecordNamespace;
    }
}
