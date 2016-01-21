package com.marklogic.client.spring.batch.core.repository;

import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import com.marklogic.client.io.DocumentMetadataHandle;

public class MarkLogicJobRepository implements JobRepository {
	
	@Autowired
	private DocumentBuilder documentBuilder;
	
	private JAXBContext jaxbContext;
	
	private DocumentMetadataHandle jobExecutionMetadata;
	
	private DatabaseClient client;
	
	public MarkLogicJobRepository(DatabaseClient client) {
		this.client = client;
		try {
			jaxbContext = JAXBContext.newInstance(org.springframework.batch.core.JobExecution.class);
		} catch (Exception ex) {
			
		}
		jobExecutionMetadata = new DocumentMetadataHandle();
		jobExecutionMetadata.getCollections().add("http://marklogic.com/spring-batch/job-execution");
		
	}

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
		jobExecution.setId(1234L);
		Document doc = documentBuilder.newDocument();
		Marshaller marshaller = null;
		try {
			JAXBElement<JobExecution> jaxbElement = new JAXBElement<JobExecution>(new QName("http://marklogic.com/spring-batch", "jobExecution"), org.springframework.batch.core.JobExecution.class, jobExecution);
			marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(jaxbElement, doc);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
		DOMHandle handle = new DOMHandle();
		handle.set(doc);
		xmlDocMgr.write("/projects.spring.io/spring-batch/job-execution/" + jobExecution.getId().toString(), jobExecutionMetadata, handle);
		
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
