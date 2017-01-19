package com.marklogic.spring.batch.item.writer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.util.EditableNamespaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

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
            String xmlPatch = item[1];
            logger.info(uri);
            XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
            EditableNamespaceContext namespaces = new EditableNamespaceContext();
            namespaces.put("html", "http://www.w3.org/1999/xhtml");
            
            DocumentPatchBuilder xmlPatchBldr = docMgr.newPatchBuilder();
            xmlPatchBldr.setNamespaces(namespaces);
        
            //note the root element is referenced in the first parameter of this call, you may need to change based on your document
            DocumentPatchHandle patchHandle = xmlPatchBldr.insertFragment("/doc", DocumentPatchBuilder.Position.LAST_CHILD, xmlPatch).build();
            docMgr.patch(uri, patchHandle);
        }
        
    }
}
