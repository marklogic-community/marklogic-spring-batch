package com.marklogic.spring.batch.core.repository;

import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.BatchJobExecution;

public class MarshallJobInstanceToXmlTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobParametersBuilder jobParametersBuilder;
	
	BatchJobExecution jobExec;
	
	Document doc;
	Marshaller marshaller;
	
	@Before
	public void setup() throws Exception {
		jobParametersBuilder.addString("stringTest", "Joe Cool", true);
		jobParametersBuilder.addDate("start", new Date(), false);
		jobParametersBuilder.addLong("longTest", 1239L, false);
		jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
		
		JobInstance jobInstance = new JobInstance(123L, "TestJobInstance");
		
		JobExecution jobExecution = new JobExecution(jobInstance, jobParametersBuilder.toJobParameters());
		
		jobExec = new BatchJobExecution(jobExecution);
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		marshaller = JAXBContext.newInstance(BatchJobExecution.class, JobParameters.class).createMarshaller();
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
        frag.assertElementValue("/sb:jobExecution/sb:jobInstance/sb:id", "123");
	}
}
