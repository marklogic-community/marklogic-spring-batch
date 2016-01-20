package com.marklogic.client.spring.batch.geonames;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;

import org.geonames.Geoname;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;

import org.jdom2.input.DOMBuilder;

import org.junit.runner.RunWith;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { com.marklogic.client.spring.batch.SpringBatchConfig.class })
public class MarshallGeonameObjectToXmlTest extends Assert {
	
	@Autowired
	Marshaller marshaller;
	
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
		marshaller.marshal(geo, w3cDoc);
		Fragment frag = new Fragment(domBuilder.build(w3cDoc));
		
		System.out.println(frag.getPrettyXml());
	
		
	}

}
