package com.marklogic.spring.batch.bind;

import com.marklogic.spring.batch.core.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JaxbConfiguration.class})
public class UnmarshallSpringBatchPojoTest extends Assert {

    private Document doc;
    private DOMSource source;

    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void unmarshallJobParameters() throws Exception {
        Resource jobParametersXml = ctx.getResource("classpath:/xml/job-parameters.xml");

        AdaptedJobParameters adParams = (AdaptedJobParameters) jaxb2Marshaller.unmarshal(new StreamSource(jobParametersXml.getInputStream()));
        JobParametersAdapter adapter = new JobParametersAdapter();
        JobParameters params = adapter.unmarshal(adParams);
        assertEquals(5, params.getParameters().size());
        assertEquals("Joe Cool", params.getString("stringTest"));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        assertEquals(df.parse("2016-05-05T20:49:24-0400").toString(), params.getDate("start").toString());
        assertEquals(Long.valueOf(1239L), params.getLong("longTest"));
        assertEquals(Double.valueOf(1.35D), params.getDouble("doubleTest"));
    }

    @Test
    public void unmarshallJobInstance() throws Exception {
        Resource jobInstanceXml = ctx.getResource("classpath:/xml/job-instance.xml");
        AdaptedJobInstance adJobInstance = (AdaptedJobInstance) jaxb2Marshaller.unmarshal(new StreamSource(jobInstanceXml.getInputStream()));
        JobInstanceAdapter adapter = new JobInstanceAdapter();
        JobInstance jobInstance = adapter.unmarshal(adJobInstance);
        assertEquals(new Long(123L), jobInstance.getId());
        assertEquals("test", jobInstance.getJobName());

    }

    @Test
    public void unmarshallJobExecution() throws Exception {
        Resource jobExecutionXml = ctx.getResource("classpath:/xml/job-execution.xml");
        AdaptedJobExecution adJobExecution = (AdaptedJobExecution) jaxb2Marshaller.unmarshal(new StreamSource(jobExecutionXml.getInputStream()));
        JobExecutionAdapter adapter = new JobExecutionAdapter();
        JobExecution jobExecution = adapter.unmarshal(adJobExecution);
        assertNotNull(jobExecution);
        assertEquals(Long.valueOf(123), jobExecution.getId());
        assertEquals("STARTING", jobExecution.getStatus().toString());
        assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void unmarshallStepExecution() throws Exception {
        Resource stepExecutionXml = ctx.getResource("classpath:/xml/step-execution.xml");
        AdaptedStepExecution adStepExecution = (AdaptedStepExecution) jaxb2Marshaller.unmarshal(new StreamSource(stepExecutionXml.getInputStream()));
        StepExecutionAdapter adapter = new StepExecutionAdapter();
        StepExecution stepExecution = adapter.unmarshal(adStepExecution);
        assertEquals("testStep", stepExecution.getStepName());
    }

    @Test
    public void unmarshallExecutionContext() throws Exception {
        Resource executionContextXml = ctx.getResource("classpath:/xml/execution-context.xml");
        AdaptedExecutionContext aec = (AdaptedExecutionContext) jaxb2Marshaller.unmarshal(new StreamSource(executionContextXml.getInputStream()));
        ExecutionContextAdapter adapter = new ExecutionContextAdapter();
        ExecutionContext executionContext = adapter.unmarshal(aec);
        assertEquals("testValue", executionContext.get("testName"));
        assertEquals(123L, executionContext.getLong("testLong"));
        assertEquals(new Double(123D), new Double(executionContext.getDouble("testDouble")));
        assertEquals(123, executionContext.getInt("testInteger"));
        assertEquals(683181905, executionContext.hashCode());
    }

}
