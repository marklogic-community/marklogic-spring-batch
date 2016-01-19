package com.marklogic.client.spring.batch.geonames;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;

import com.marklogic.client.io.DOMHandle;

import org.geonames.Geoname;
import org.junit.Test;

public class GeonameObjectToXmlTest {
	
	@Test
	public void marshallGeonameTest() {
		Geoname geo = new Geoname();
		geo.setId("123");
		List<String> names = new ArrayList<String>();
		names.add("Alpha");
		names.add("Beta");
		geo.setNames(names);
		
		try  {
			JAXBContext context = JAXBContext.newInstance(Geoname.class);
			Marshaller marshall = context.createMarshaller();
			DOMHandle handle = new DOMHandle();
			DocumentBuilder builder = handle.getFactory().newDocumentBuilder();
			marshall.marshal(geo, System.out);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}
