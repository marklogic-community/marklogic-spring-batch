package com.marklogic.spring.batch.bind;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

@ActiveProfiles(profiles = "marklogic", inheritProfiles = false)
public class UnmarshallSpringBatchPojoTest extends AbstractSpringBatchTest {
	
	@Autowired
	private JAXBContext jaxbContext;
	
	@Autowired
	private ApplicationContext ctx;
	
	private Unmarshaller unmarshaller;
	
	@Before
	public void setup() throws Exception {
		unmarshaller = jaxbContext.createUnmarshaller();
	}
	
	@Test
	public void unmarshallJobParameters() throws Exception {
		Resource jobParametersXml = ctx.getResource("classpath:/xml/job-parameters.xml");
		AdaptedJobParameters adParams = (AdaptedJobParameters)unmarshaller.unmarshal(jobParametersXml.getInputStream());
		JobParametersAdapter adapter = new JobParametersAdapter();
		JobParameters params = adapter.unmarshal(adParams);
		assertEquals(4, params.getParameters().size());
		assertEquals("Joe Cool", params.getString("stringTest"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		assertEquals(df.parse("2016-03-10T15:22:45-0500").toString(), params.getDate("start").toString());
		assertEquals(Long.valueOf(1239L), params.getLong("longTest"));
		assertEquals(Double.valueOf(1.35D), params.getDouble("doubleTest"));
	}
	
	@Test
	public void unmarshallJobInstance() throws Exception {
		Resource jobInstanceXml = ctx.getResource("classpath:/xml/job-instance.xml");
		AdaptedJobInstance adJobInstance = (AdaptedJobInstance)unmarshaller.unmarshal(jobInstanceXml.getInputStream());
		JobInstanceAdapter adapter = new JobInstanceAdapter();
		JobInstance jobInstance = adapter.unmarshal(adJobInstance);
		assertEquals(new Long(123L), jobInstance.getId());
		assertEquals("test", jobInstance.getJobName());
		
	}
	
	@Test
	public void unmarshallJobExecution() throws Exception {
		Resource jobExecutionXml = ctx.getResource("classpath:/xml/job-execution.xml");
		AdaptedJobExecution jobExecution = (AdaptedJobExecution)unmarshaller.unmarshal(jobExecutionXml.getInputStream());
		assertEquals(MarkLogicSpringBatch.SPRING_BATCH_DIR + "12345.xml", jobExecution.getUri());
		assertEquals(Long.valueOf(12345L), jobExecution.getId());
		
		JobExecutionAdapter adapter = new JobExecutionAdapter();
		JobExecution jobExec = adapter.unmarshal(jobExecution);
		assertNotNull(jobExec);
		assertEquals(Long.valueOf(12345L), jobExecution.getId());
	}
	
	@Test
	public void unmarshallStepExecution() throws Exception {
		Resource stepExecutionXml = ctx.getResource("classpath:/xml/step-execution.xml");
		AdaptedStepExecution adStepExecution = (AdaptedStepExecution)unmarshaller.unmarshal(stepExecutionXml.getInputStream());
		StepExecutionAdapter adapter = new StepExecutionAdapter();
		StepExecution stepExecution = adapter.unmarshal(adStepExecution);
		assertEquals("testStep", stepExecution.getStepName());
	}

}
