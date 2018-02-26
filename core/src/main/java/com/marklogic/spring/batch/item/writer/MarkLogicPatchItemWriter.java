package com.marklogic.spring.batch.item.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.EditableNamespaceContext;
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

public class MarkLogicPatchItemWriter implements ItemWriter<String[]> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    
    public MarkLogicPatchItemWriter(DatabaseClient client) {
        this.databaseClient = client;
    }

    @Override
    public void write(List<? extends String[]> items) throws Exception {
        for (String[] item : items) {
            String uri = item[0];
            String itemPatch = item[1];
            logger.info(uri);

            XMLDocumentManager docXmlMgr = databaseClient.newXMLDocumentManager();
            JSONDocumentManager docJsonMgr = databaseClient.newJSONDocumentManager();

            EditableNamespaceContext namespaces = new EditableNamespaceContext();
            namespaces.put("html", "http://www.w3.org/1999/xhtml");

            DocumentPatchBuilder xmlPatchBldr = docXmlMgr.newPatchBuilder();
            xmlPatchBldr.setNamespaces(namespaces);

            DocumentPatchBuilder jsonPatchBldr = docJsonMgr.newPatchBuilder();

            Boolean isXml = false;

            if (itemPatch.startsWith("<")) {isXml = checkIfXMLIsWellFormed(itemPatch); }

            Boolean isJson = isJSONValid(itemPatch);

            if (isXml) {
                //note the root element is referenced in the first parameter of this call, you may need to change based on your xml document
                DocumentPatchHandle patchHandle = xmlPatchBldr.insertFragment("/doc", DocumentPatchBuilder.Position.LAST_CHILD, itemPatch).build();
                docXmlMgr.patch(uri, patchHandle);
            }

            if (isJson)
            {
                //note the root node is referenced in the first parameter of this call, you may need to change based on your json document
                DocumentPatchHandle patchHandle = jsonPatchBldr.insertFragment("a", DocumentPatchBuilder.Position.LAST_CHILD, itemPatch).build();
                docJsonMgr.patch(uri, patchHandle);
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


    public static boolean isJSONValid(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
