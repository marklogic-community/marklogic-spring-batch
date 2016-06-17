package com.marklogic.example.geonames;

import org.geonames.Geoname;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.batch.item.ItemProcessor;
import org.w3c.dom.Document;

public class GeonamesItemProcessor implements ItemProcessor<Geoname, Document> {

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
	};


	@Override
	public Document process(Geoname item) throws Exception {
		Document doc = documentBuilder().newDocument();
		try {
			Marshaller marshaller = jaxbContext().createMarshaller();
			marshaller.marshal(item, doc);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		//Set document URI
		doc.setDocumentURI("http://geonames.org/geoname/" + item.getId());
		
		return doc;
	}

}
