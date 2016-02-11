package com.marklogic.spring.batch.core.repository;

import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.bind.AdaptedJobExecution;

public class MarshallJobInstanceToXmlTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JobParametersBuilder jobParametersBuilder;
	
	JobInstance jobInstance;
	
	Document doc;
	Marshaller marshaller;
	
	@Before
	public void setup() throws Exception {
		jobParametersBuilder.addString("stringTest", "Joe Cool", true);
		jobParametersBuilder.addDate("start", new Date(), false);
		jobParametersBuilder.addLong("longTest", 1239L, false);
		jobParametersBuilder.addDouble("doubleTest", 1.35D, false);
		
		jobInstance = new JobInstance(123L, "TestJobInstance");
		
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		marshaller = JAXBContext.newInstance(AdaptedJobExecution.class, JobParameters.class).createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}
	
	@Test
    public void marshallJobParametersTest() throws Exception {
		AdaptedJobExecution jobExec = new AdaptedJobExecution();
		jobExec.setJobParameters(jobParametersBuilder.toJobParameters());
        marshaller.marshal(jobExec, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.assertElementExists("/sb:job/sb:jobParameters/sb:jobParameter[@key = 'stringTest' and text() = 'Joe Cool' and @identifier = 'true']");
        frag.assertElementExists("/sb:job/sb:jobParameters/sb:jobParameter[@key = 'longTest' and text() = '1239' and @identifier = 'false']");
        frag.assertElementExists("/sb:job/sb:jobParameters/sb:jobParameter[@key = 'start' and @identifier = 'false']");
        frag.assertElementExists("/sb:job/sb:jobParameters/sb:jobParameter[@key = 'doubleTest' and text() = '1.35' and @identifier = 'false']");
        frag.prettyPrint();
    }
	
	@Ignore
	@Test
	public void marshallJobInstanceTest() throws Exception {
		JAXBElement<JobInstance> element = new JAXBElement<JobInstance>(new QName(MarkLogicSpringBatchRepository.NAMESPACE, "jobInstance"), JobInstance.class, jobInstance);
		marshaller.marshal(element, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementValue("/sb:jobInstance/sb:id", "123");
	}

	@Ignore
	@Test
	public void marshallJobExecutionTest() throws Exception {
		JobExecution jobExecution = new JobExecution(jobInstance, jobParametersBuilder.toJobParameters());
		AdaptedJobExecution xmlJobExecution = new AdaptedJobExecution();
		//xmlJobExecution.setJobExecution(jobExecution);
		//xmlJobExecution.setJobInstance(jobInstance);
		//xmlJobExecution.setJobParameters(jobParams);
		marshaller.marshal(xmlJobExecution, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementValue("/sb:job/sb:jobExecution/sb:jobInstance/sb:id", "123");
	}
}
