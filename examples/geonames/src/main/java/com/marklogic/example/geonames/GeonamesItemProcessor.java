package com.marklogic.example.geonames;

import org.geonames.Geoname;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

public class GeonamesItemProcessor implements ItemProcessor<Geoname, Document> {
	
	@Autowired
	private JAXBContext jaxbContext;
	
	@Autowired
	private DocumentBuilder documentBuilder;

	@Override
	public Document process(Geoname item) throws Exception {
		Document doc = documentBuilder.newDocument();
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(item, doc);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		System.out.println(item.getId());
		
		//Set document URI
		doc.setDocumentURI("http://geonames.org/geoname/" + item.getId());
		
		return doc;
	}

}
