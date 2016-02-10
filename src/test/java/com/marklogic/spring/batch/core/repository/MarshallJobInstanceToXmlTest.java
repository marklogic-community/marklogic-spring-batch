package com.marklogic.spring.batch.core.repository;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Test;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;

public class MarshallJobInstanceToXmlTest extends AbstractSpringBatchTest {
	
	@Test
    public void marshallJobParametersTest() throws Exception {
		JobParameter param = new JobParameter("Big Time");
		Map<String, JobParameter> jobParamMap = new HashMap<String, JobParameter>();
		jobParamMap.put("test", param);
        JobParameters params = new JobParameters(jobParamMap);
        
        com.marklogic.spring.batch.bind.JobParameters jobParams = new com.marklogic.spring.batch.bind.JobParameters(params.toProperties());

        Document w3cDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(com.marklogic.spring.batch.bind.JobParameters.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        marshaller.marshal(jobParams, w3cDoc);
        Fragment frag = new Fragment(new DOMBuilder().build(w3cDoc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces());
        frag.prettyPrint();
        
        frag.assertElementExists("//sb:property");
     
    }
}
