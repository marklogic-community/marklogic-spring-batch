package com.marklogic.spring.batch.bind;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.AdaptedJobExecution;

public class MarshallJobInstanceToXmlTest extends AbstractSpringBatchTest {
	
	AdaptedJobExecution jobExec;
	
	Document doc;
	Marshaller marshaller;
	
	@Before
	public void setup() throws Exception {
		
		JobInstance jobInstance = new JobInstance(123L, "TestJobInstance");
		
		JobExecution jobExecution = new JobExecution(jobInstance, newJobParametersUtils().getJobParameters());
		
		jobExec = new AdaptedJobExecution(jobExecution);
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		marshaller = JAXBContext.newInstance(AdaptedJobExecution.class).createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(jobExec, doc);
	}
	
	@Test
    public void marshallJobParametersTest() throws Exception {
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.assertElementExists("/sb:jobExecution/sb:jobParameters/sb:jobParameter[@key = 'stringTest' and text() = 'Joe Cool' and @identifier = 'true']");
        frag.assertElementExists("/sb:jobExecution/sb:jobParameters/sb:jobParameter[@key = 'longTest' and text() = '1239' and @identifier = 'false']");
        frag.assertElementExists("/sb:jobExecution/sb:jobParameters/sb:jobParameter[@key = 'start' and @identifier = 'false']");
        frag.assertElementExists("/sb:jobExecution/sb:jobParameters/sb:jobParameter[@key = 'doubleTest' and text() = '1.35' and @identifier = 'false']");
        frag.prettyPrint();
    }
	
	@Test
	public void marshallJobInstanceTest() throws Exception {
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementValue("/sb:jobExecution/sb:jobInstance/sb:id", "123");
	}

	@Test
	public void marshallJobExecutionTest() throws Exception {
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementExists("/sb:jobExecution/sb:createDateTime");
        frag.assertElementValue("/sb:jobExecution/sb:status", "STARTING");
	}
}
