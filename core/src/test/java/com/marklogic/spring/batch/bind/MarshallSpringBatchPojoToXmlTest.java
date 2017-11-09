package com.marklogic.spring.batch.bind;

import com.marklogic.junit.Fragment;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.spring.batch.JobExecutionTestUtils;
import com.marklogic.spring.batch.JobParametersTestUtils;
import com.marklogic.spring.batch.SpringBatchNamespaceProvider;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import org.jdom2.input.DOMBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JaxbConfiguration.class})
public class MarshallSpringBatchPojoToXmlTest {

    private Document doc;
    private DOMResult result;

    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller;

    @Before
    public void setup() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        result = new DOMResult(doc);
    }

    @Test
    public void marshallJobParametersTest() throws Exception {
        JobParameters params = JobParametersTestUtils.getJobParameters();
        JobParametersAdapter adapter = new JobParametersAdapter();
        AdaptedJobParameters adaptedParams = adapter.marshal(params);
        jaxb2Marshaller.marshal(adaptedParams, result);
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
        jaxb2Marshaller.marshal(adapter.marshal(jobInstance), result);
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
        jaxb2Marshaller.marshal(adapter.marshal(jobExecution), result);
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
        jaxb2Marshaller.marshal(adStep, result);
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
        jaxb2Marshaller.marshal(adapter.marshal(ec), result);
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

    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

}
