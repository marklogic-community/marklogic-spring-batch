package com.marklogic.spring.batch.bind;

import java.util.Date;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.JobExecutionTestUtils;
import com.marklogic.spring.batch.JobParametersTestUtils;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicJobInstance;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class MarshallSpringBatchPojoToXmlTest extends AbstractSpringBatchTest {
	
	Document doc;
	Marshaller marshaller;
	
	@Before
	public void setup() throws Exception {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();	
		marshaller = jaxbContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}
	
	@Test
    public void marshallJobParametersTest() throws Exception {
		JobParameters params = JobParametersTestUtils.getJobParameters();
		JobParametersAdapter adapter = new JobParametersAdapter();
		AdaptedJobParameters adaptedParams = adapter.marshal(params);
	    marshaller.marshal(adaptedParams, doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementExists("/msb:jobParameters/msb:jobParameter[@key = 'stringTest' and text() = 'Joe Cool' and @identifier = 'true']");
        frag.assertElementExists("/msb:jobParameters/msb:jobParameter[@key = 'longTest' and text() = '1239' and @identifier = 'false']");
        frag.assertElementExists("/msb:jobParameters/msb:jobParameter[@key = 'start' and @identifier = 'false']");
        frag.assertElementExists("/msb:jobParameters/msb:jobParameter[@key = 'doubleTest' and text() = '1.35' and @identifier = 'false']");
    }
	
	@Test
	public void marshallJobInstanceTest() throws Exception {
		JobInstance jobInstance = new JobInstance(123L, "test");
		JobInstanceAdapter adapter = new JobInstanceAdapter();
	    marshaller.marshal(adapter.marshal(jobInstance), doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementValue("/msb:jobInstance/msb:id", "123");
        frag.assertElementValue("/msb:jobInstance/msb:jobName", "test");
	}

	@Test
	public void marshallJobExecutionTest() throws Exception {
		JobExecution jobExecution = JobExecutionTestUtils.getJobExecution();
		JobExecutionAdapter adapter = new JobExecutionAdapter();
	    marshaller.marshal(adapter.marshal(jobExecution), doc);
        Fragment frag = new Fragment(new DOMBuilder().build(doc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces()); 
        frag.prettyPrint();
        frag.assertElementExists("/msb:jobExecution/msb:id");
        frag.assertElementExists("/msb:jobExecution/msb:createDateTime");
        frag.assertElementValue("/msb:jobExecution/msb:status", "STARTING");
        frag.assertElementExists("/msb:jobExecution/msb:jobInstance");
        frag.assertElementExists("/msb:jobExecution/msb:jobParameters");
        frag.assertElementExists("/msb:jobExecution/msb:stepExecutions");
        frag.assertElementExists("/msb:jobExecution/msb:executionContext");
        List<Fragment> steps = frag.getFragments("msb:jobExecution/msb:stepExecutions/msb:stepExecution");
      	steps.get(0).assertElementValue("/msb:stepExecution/msb:stepName", "sampleStep1");
      	steps.get(1).assertElementValue("/msb:stepExecution/msb:stepName", "sampleStep2");
	}
	
	@Test
	public void marshallStepExecutionTest() throws Exception {
		JobInstance jobInstance = new JobInstance(1234L, "test");
		JobExecution jobExecution = new JobExecution(123L);
		jobExecution.setJobInstance(jobInstance);
		StepExecution step = new StepExecution("testStep", jobExecution);	
		step.setLastUpdated(new Date(System.currentTimeMillis()));
		StepExecutionAdapter adapter = new StepExecutionAdapter();
		AdaptedStepExecution adStep = adapter.marshal(step);
	    marshaller.marshal(adStep, doc);
		Fragment frag = new Fragment(new DOMBuilder().build(doc));
		frag.setNamespaces(getNamespaceProvider().getNamespaces());
		frag.prettyPrint();
		frag.assertElementExists("/msb:stepExecution");
		frag.assertElementExists("/msb:stepExecution/msb:lastUpdated");
		frag.assertElementValue("/msb:stepExecution/msb:stepName", "testStep");
	}
	
	@Test
	public void marshallExecutionContextTest() throws Exception {
		ExecutionContext ec = new ExecutionContext();
		ec.putString("testName", "testValue");
		ec.putLong("testLong", 123L);
		ec.putDouble("testDouble", 123D);
		ec.putInt("testInteger", 123);
		ExecutionContextAdapter adapter = new ExecutionContextAdapter();
		marshaller.marshal(adapter.marshal(ec), doc);
		Fragment frag = new Fragment(new DOMBuilder().build(doc));
		frag.setNamespaces(getNamespaceProvider().getNamespaces());
		frag.prettyPrint();
		frag.assertElementExists("/msb:executionContext/msb:map/entry/key[text() = 'testName']");
		frag.assertElementExists("/msb:executionContext/msb:map/entry/value[@xsi:type = 'xs:int'][text() = '123']");
		frag.assertElementExists("/msb:executionContext/msb:map/entry/value[@xsi:type = 'xs:long'][text() = '123']");
		frag.assertElementExists("/msb:executionContext/msb:map/entry/value[@xsi:type = 'xs:string'][text() = 'testValue']");
		frag.assertElementExists("/msb:executionContext/msb:map/entry/value[@xsi:type = 'xs:double'][text() = '123.0']");
		frag.assertElementExists("/msb:executionContext/msb:hashCode");
	}
	
	@Test
	public void marshallMarkLogicJobInstanceTest() throws Exception {
		JobExecution jobExecution = JobExecutionTestUtils.getJobExecution();
		jobExecution.createStepExecution("stepA");
		jobExecution.createStepExecution("stepB");
		MarkLogicJobInstance mji = new MarkLogicJobInstance(jobExecution.getJobInstance());
		mji.addJobExecution(jobExecution);
		marshaller.marshal(mji, doc);
		Fragment frag = new Fragment(new DOMBuilder().build(doc));
		frag.setNamespaces(getNamespaceProvider().getNamespaces());
		frag.prettyPrint();
		
	}
}
