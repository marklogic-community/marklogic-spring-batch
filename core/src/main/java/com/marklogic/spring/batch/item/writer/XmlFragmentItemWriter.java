package com.marklogic.spring.batch.item.writer;


import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPatchBuilder;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import jdk.internal.org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XmlFragmentItemWriter implements ItemWriter<Map<String,String>> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    private DocumentPatchBuilder.Position contextPosition;
    private String xmlContext;


    //contextPath is the context path of the document which will be updated with the patch content. Position is the updating position of the contextPath
    public XmlFragmentItemWriter(DatabaseClient client, String contextPath, DocumentPatchBuilder.Position contextPosition) {
        this.databaseClient = client;
        xmlContext = contextPath;
        this.contextPosition = contextPosition;
    }

    //A list of items. Each item is Map type. The key of Map is the uri of the document in the database. The value is the patch content.
    //The document is updating with the patch content
     @Override
    public void write(List<? extends Map<String,String>> items) throws Exception {
        for (Map<String,String> item : items) {

            String uri = item.keySet().toArray()[0].toString();
            String itemPatch = item.get(uri);
            logger.info("uri" + uri);
            logger.info("itemPatch" + itemPatch);

            XMLDocumentManager docXmlMgr = databaseClient.newXMLDocumentManager();

            EditableNamespaceContext namespaces = new EditableNamespaceContext();
            namespaces.put("html", "http://www.w3.org/1999/xhtml");

            DocumentPatchBuilder xmlPatchBldr = docXmlMgr.newPatchBuilder();
            xmlPatchBldr.setNamespaces(namespaces);

            Boolean isXml = false;

            if (itemPatch.startsWith("<")) {isXml = checkIfXMLIsWellFormed(itemPatch); }


            if (isXml) {
                //note the root element is referenced in the first parameter of this call, you may need to change based on your xml document
                DocumentPatchHandle patchHandle = xmlPatchBldr.insertFragment(xmlContext, contextPosition, itemPatch).build();
                docXmlMgr.patch(uri, patchHandle);
            }

        }

    }


    private boolean checkIfXMLIsWellFormed(String xml) throws SAXException, IOException, org.xml.sax.SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        parser.setContentHandler(new DefaultHandler());
        InputSource source = new InputSource(new ByteArrayInputStream(xml.getBytes()));
        try {
            parser.parse(source);
            return true;
        } catch (org.xml.sax.SAXException e) {
            System.out.println(xml + "not well formed");
            return false;
        }
    }



}
