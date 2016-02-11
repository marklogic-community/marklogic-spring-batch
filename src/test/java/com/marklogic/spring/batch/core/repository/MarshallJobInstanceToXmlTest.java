package com.marklogic.spring.batch.core.repository;

import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.bind.XmlJobParameters;

public class MarshallJobInstanceToXmlTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobParametersBuilder jobParametersBuilder;
	
	XmlJobParameters jobParams;
	
	@Before
	public void setup() {
		jobParametersBuilder.addString("stringTest", "Joe Cool", true);
		jobParametersBuilder.addDate("start", new Date(), false);
		jobParametersBuilder.addLong("longTest", 1239L, false);
		jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
		jobParams = new XmlJobParameters(jobParametersBuilder.toJobParameters());
	}
	
	@Test
    public void marshallJobParametersTest() throws Exception {
        Document w3cDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(XmlJobParameters.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jobParams, w3cDoc);
        Fragment frag = new Fragment(new DOMBuilder().build(w3cDoc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.assertElementExists("/sb:jobParameters/sb:jobParameter[@key = 'stringTest' and text() = 'Joe Cool' and @identifier = 'true']");
        frag.assertElementExists("/sb:jobParameters/sb:jobParameter[@key = 'longTest' and text() = '1239' and @identifier = 'false']");
        frag.assertElementExists("/sb:jobParameters/sb:jobParameter[@key = 'start' and @identifier = 'false']");
        frag.assertElementExists("/sb:jobParameters/sb:jobParameter[@key = 'doubleTest' and text() = '1.35' and @identifier = 'false']");
        frag.prettyPrint();
    }
}
