package com.marklogic.client.spring.batch.core.repository;

import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;

public class MarkLogicJobRepository implements JobRepository {
	
	@Autowired
	private DocumentBuilder documentBuilder;
	
	@Autowired
	JAXBContext jaxbContext;
	
	public MarkLogicJobRepository(DatabaseClient client) {
		this.client = client;
	}
	private DatabaseClient client;

	@Override
	public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		Document doc = documentBuilder.newDocument();
		doc.createElement("test");
		DOMHandle handle = new DOMHandle();
		handle.set(doc);
		xmlDocMgr.write("/test.xml", handle);
		JobInstance instance = new JobInstance(12L, jobName);
		return instance;
	}

	@Override
	public JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters,
			String jobConfigurationLocation) {
			JobExecution jobExecution = new JobExecution(jobInstance, jobParameters, jobConfigurationLocation);
			Document doc = documentBuilder.newDocument();
			Marshaller marshaller = null;
			try {
			
				marshaller = jaxbContext.createMarshaller();
				marshaller.marshal(jobExecution, doc);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
			DOMHandle handle = new DOMHandle();
			handle.set(doc);
			xmlDocMgr.write("/tes123t.xml", handle);
			
		return jobExecution;
	}

	@Override
	public JobExecution createJobExecution(String jobName, JobParameters jobParameters)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		JobInstance jobInstance = new JobInstance(123L, "name1");
		JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
		jobExecution.setId(123L);
		Document doc = documentBuilder.newDocument();
		Marshaller marshaller = null;
		try {
			JAXBIntrospector introspector = jaxbContext.createJAXBIntrospector();
			JAXBElement jaxbElement = new JAXBElement(new QName("jobExecution"), JobExecution.class, jobExecution);
			marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(jaxbElement, doc);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		DOMHandle handle = new DOMHandle();
		handle.set(doc);
		xmlDocMgr.write("/spring-batch/jobExecution/" + jobExecution.getId().toString(), handle);
		
		return jobExecution;
	}

	@Override
	public void update(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(StepExecution stepExecution) {
		System.out.println(stepExecution.getStepName());
		
	}

	@Override
	public void addAll(Collection<StepExecution> stepExecutions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateExecutionContext(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateExecutionContext(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStepExecutionCount(JobInstance jobInstance, String stepName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
