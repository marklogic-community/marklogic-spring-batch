package com.marklogic.client.spring.batch.geonames;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;

import org.geonames.Geoname;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.client.spring.batch.AbstractSpringBatchTest;
import com.marklogic.junit.Fragment;

import org.jdom2.input.DOMBuilder;

public class MarshallGeonameObjectToXmlTest extends AbstractSpringBatchTest {
	
	@Autowired
	JAXBContext jaxbContext;
	
	@Autowired
	DocumentBuilder documentBuilder;
	
	@Autowired
	DOMBuilder domBuilder;
	
	@Test
	public void marshallGeonameTest() throws JAXBException {
		Geoname geo = new Geoname();
		geo.setId("123");
		List<String> names = new ArrayList<String>();
		names.add("Alpha");
		names.add("Beta");
		geo.setNames(names);
		
		Document w3cDoc = documentBuilder.newDocument();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.marshal(geo, w3cDoc);
		Fragment frag = new Fragment(domBuilder.build(w3cDoc));
		frag.setNamespaces(getNamespaceProvider().getNamespaces());
		System.out.println(frag.getPrettyXml());
		frag.assertElementExists("/geo:geoname/geo:names/geo:name[2][text() = 'Beta']");	
	}

}
