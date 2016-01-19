package com.marklogic.client.spring.batch.writer;

import java.util.List;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;

import org.w3c.dom.Document;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class DocumentItemWriter implements ItemWriter<Document> {

	@Autowired
	private Environment env;
	
	@Override
	public void write(List<? extends Document> items) throws Exception {
		DatabaseClient client = DatabaseClientFactory.newClient(
				   env.getProperty("marklogic.host"), Integer.parseInt(env.getProperty("marklogic.port")), 
				   env.getProperty("marklogic.user"), env.getProperty("marklogic.password"), Authentication.DIGEST);
		XMLDocumentManager docMgr = client.newXMLDocumentManager();
		for (int i = 0; i < items.size(); i++) {
			Document doc = items.get(i);
			DOMHandle handle = new DOMHandle(doc);
			docMgr.write(doc.getDocumentURI(), handle);
		}		
	}

}
