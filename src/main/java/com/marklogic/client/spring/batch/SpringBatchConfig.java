package com.marklogic.client.spring.batch;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.input.DOMBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBatchConfig {
	
	@Bean
	public Marshaller marshaller() {
		Marshaller marshaller = null;
		
		try {
			JAXBContext context = JAXBContext.newInstance(org.geonames.Geoname.class);
			marshaller = context.createMarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return marshaller;
	}
	
	@Bean
	public DocumentBuilder documentBuilder() {
		DocumentBuilder docBuilder = null;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true);
			docBuilder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return docBuilder;
	}
	
	@Bean
	public DOMBuilder domBuilder() {
		return new DOMBuilder();
	}
	
}
