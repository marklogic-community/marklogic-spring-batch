package com.marklogic.spring.batch.bind;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobExecutionTestUtils;
import com.marklogic.spring.batch.JobParametersTestUtils;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class MarshallSpringBatchPojoToXmlTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JAXBContext jaxbContext;
	
	Document doc;
	Marshaller marshaller;
	
	@Before
	public void setup() throws Exception {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();	
	}
	
	@Test
    public void marshallJobParametersTest() throws Exception {
		JobParameters params = JobParametersTestUtils.getJobParameters();
		JobParametersAdapter adapter = new JobParametersAdapter();
		AdaptedJobParameters adaptedParams = adapter.marshal(params);
		Marshaller marshaller = jaxbContext.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(adaptedParams, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.assertElementExists("/jp:jobParameters/jp:jobParameter[@key = 'stringTest' and text() = 'Joe Cool' and @identifier = 'true']");
        frag.assertElementExists("/jp:jobParameters/jp:jobParameter[@key = 'longTest' and text() = '1239' and @identifier = 'false']");
        frag.assertElementExists("/jp:jobParameters/jp:jobParameter[@key = 'start' and @identifier = 'false']");
        frag.assertElementExists("/jp:jobParameters/jp:jobParameter[@key = 'doubleTest' and text() = '1.35' and @identifier = 'false']");
        frag.prettyPrint();
    }
	
	@Test
	public void marshallJobInstanceTest() throws Exception {
		AdaptedJobInstance jobInstance = new AdaptedJobInstance(123L, "test");	
		Marshaller marshaller = JAXBContext.newInstance(AdaptedJobInstance.class).createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(jobInstance, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementValue("/inst:jobInstance/inst:id", "123");
        frag.assertElementValue("/inst:jobInstance/inst:jobName", "test");
	}

	@Test
	public void marshallJobExecutionTest() throws Exception {
		AdaptedJobExecution adaptedJobExecution = new AdaptedJobExecution(JobExecutionTestUtils.getJobExecution());
		marshaller = JAXBContext.newInstance(AdaptedJobExecution.class).createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(adaptedJobExecution, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementExists("/msb:jobExecution/msb:id");
        frag.assertElementExists("/msb:jobExecution/msb:createDateTime");
        frag.assertElementValue("/msb:jobExecution/msb:status", "STARTING");
	}
	
	@Test
	public void marshallStepExecutionTest() throws Exception {
		Fragment frag = new Fragment(new DOMBuilder().build(doc));
		frag.setNamespaces(getNamespaceProvider().getNamespaces());
		frag.prettyPrint();
		List<Fragment> steps = frag.getFragments("/msb:jobExecution/msb:stepExecutions/msb:stepExecution");
		steps.get(0).assertElementValue("/msb:stepExecution/msb:stepName", "sampleStep1");
		steps.get(1).assertElementValue("/msb:stepExecution/msb:stepName", "sampleStep2");
		
	}
}
